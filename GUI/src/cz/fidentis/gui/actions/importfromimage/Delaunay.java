/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.importfromimage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Marek Zuzi
 */
public class Delaunay {
    private ArrayList<Vertex> verts;
    private static final double EPS = 0.0001d;
    
    public Delaunay(List<Vector3f> vertices) {
        if(vertices == null || vertices.size() < 3) {
            throw new IllegalArgumentException("Delaunay triangulation requires at least 3 points.");
        }
        
        verts = new ArrayList<>(vertices.size());
        for(Vector3f vec : vertices) {
            Vertex v = new Vertex(vec);
            v.index = verts.size();
            verts.add(v);
        }
    }

    public ArrayList<Triangle> delaunay() {
        if (verts == null || verts.size() < 3) {
            return new ArrayList<>();
        }

        DistanceComparator distComp = new DistanceComparator(verts.get(0));
        Vertex v0 = verts.get(0);

        Vertex vClosest = verts.get(1);
        for (int i = 2; i < verts.size(); i++) {
            Vertex v = verts.get(i);
            if (distComp.compare(vClosest, v) > 0) {
                vClosest = v;
            }
        }

        ActiveEdge e1 = new ActiveEdge(v0, vClosest);
        Vertex vMin = findNearestOnLeft(verts, e1);
        if (vMin == null) {
            e1 = new ActiveEdge(vClosest, v0);
            vMin = findNearestOnLeft(verts, e1);
        }

        ActiveEdge e2 = new ActiveEdge(e1.v, vMin);
        ActiveEdge e3 = new ActiveEdge(vMin, e1.u);

        ArrayList<Triangle> result = new ArrayList<Triangle>();
        ArrayList<ActiveEdge> q = new ArrayList<ActiveEdge>();

        Triangle t = new Triangle(e1, e2, e3);
        result.add(t);
        e1.setIncidentTriangle(t);
        e2.setIncidentTriangle(t);
        e3.setIncidentTriangle(t);

        q.add(e1.makeTwin());
        q.add(e2.makeTwin());
        q.add(e3.makeTwin());
        while (q.size() > 0) {
            e1 = q.remove(q.size() - 1);

            vMin = findNearestOnLeft(verts, e1);
            if (vMin != null) {
                e2 = new ActiveEdge(e1.v, vMin);
                e3 = new ActiveEdge(vMin, e1.u);

                t = new Triangle(e1, e2, e3);
                e1.setIncidentTriangle(t);
                e2.setIncidentTriangle(t);
                e3.setIncidentTriangle(t);
                result.add(t);

                checkQueue(q, e2.makeTwin());
                checkQueue(q, e3.makeTwin());
            }
        }

        return result;
    }
    
    private double isLeft(Vertex v1, Vertex v2, Vertex v3) {
        return ((v2.position.x - v1.position.x) * (v3.position.y - v1.position.y))
                - ((v2.position.y - v1.position.y) * (v3.position.x - v1.position.x));
    }

    private Vertex findNearestOnLeft(Collection<Vertex> vertices, ActiveEdge e) {
        Vertex vMin = null;
        double dMin = 100000;
        for (Vertex v : vertices) {
            if (v == e.u || v == e.v) {
                continue;
            }
            if (isLeft(e.v, e.u, v) > 0) {
            } else {
                continue;
            }

            double d = delaunayDistance(e, v);
            if (d < dMin) {
                dMin = d;
                vMin = v;
            }
        }
        return vMin;
    }

    private Vertex getCircumCircle(Vertex v1, Vertex v2, Vertex v3) {
        ActiveEdge e = new ActiveEdge(v1, v2);
        ActiveEdge a = new ActiveEdge(e.v, v3);
        ActiveEdge b = new ActiveEdge(v3, e.u);

        Vector3f ma = a.getMiddle().position;
        Vector3f mb = b.getMiddle().position;
        Vector3f na = a.getPerpendicular().position;
        Vector3f nb = b.getPerpendicular().position;

        float u = ((na.x * (mb.y - ma.y)) + (na.y * (ma.x - mb.x))) / ((nb.x * na.y) - (nb.y * na.x));
        float t1 = ((ma.x - mb.x) * nb.y + (mb.y - ma.y) * nb.x) / (na.y * nb.x - na.x * nb.y);

        Vector3f S = new Vector3f(ma.x + (t1 * na.x), ma.y + (t1 * na.y), 0);
        return new Vertex(S);
    }

    private double delaunayDistance(ActiveEdge e, Vertex v) {
        Vertex S = getCircumCircle(e.u, e.v, v);

        double sideV = isLeft(e.u, e.v, v);
        double sideS = isLeft(e.u, e.v, S);

        double r = S.distance(v);

        if (sideV * sideS > 0) {
            return r;
        } else {
            return -r;
        }
    }

    private void checkQueue(ArrayList<ActiveEdge> queue, ActiveEdge edge) {
        ActiveEdge swapped = edge.swap();
        // if present in queue, remove it
        int idx = queue.indexOf(swapped);
        if (idx != -1) {
            ActiveEdge removed = queue.remove(idx);
            removed.twin().setTwin(edge.twin());
            edge.twin().setTwin(removed.twin());
        } else {
            queue.add(edge);
        }
    }

    public class Vertex {
        private Vector3f position;
        public int index = -1;
        
        public Vertex(Vector3f v) {
            position = v;
        }
        
        public double distance(Vertex v) {
            Vector3f d = new Vector3f(position);
            d.sub(v.position);
            return d.length();
        }
    }

    public class ActiveEdge {
        public Vertex u;
        public Vertex v;
        private Triangle t;
        private ActiveEdge twin;
        
        public Vertex getMiddle() {
            Vector3f mid = new Vector3f(u.position);
            mid.add(v.position);
            mid.scale(0.5f);
            return new Vertex(mid);
        }
        
        public ActiveEdge(Vertex u, Vertex v) {
            this.u = u;
            this.v = v;
        }
        
        public void setIncidentTriangle(Triangle t) {
            this.t = t;
        }
        
        public Triangle incidentTriangle() {
            return t;
        }
        
        public void setTwin(ActiveEdge twin) {
            this.twin = twin;
        }
        
        public ActiveEdge twin() {
            return twin;
        }

        public ActiveEdge swap() {
            return new ActiveEdge(this.v, this.u);
        }

        public ActiveEdge makeTwin() {
            ActiveEdge t = this.swap();
            t.twin = this;
            this.twin = t;
            return t;
        }
        
        public Vertex getPerpendicular() {
            Vector3f result = new Vector3f(v.position);
            result.sub(u.position);
            return new Vertex(new Vector3f(result.y, -result.x, 0));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof ActiveEdge)) {
                return false;
            }

            ActiveEdge e = (ActiveEdge) o;
            return this.u == e.u && this.v == e.v;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + u.hashCode();
            hash = 31 * hash + v.hashCode();
            return hash;
        }
    }

    public class Triangle {
        private ActiveEdge e1;
        private ActiveEdge e2;
        private ActiveEdge e3;
        public Triangle(ActiveEdge e1, ActiveEdge e2, ActiveEdge e3) {
            this.e1 = e1;
            this.e2 = e2;
            this.e3 = e3;
        }
        
        public Vertex getVertex(int i) {
            switch (i % 3) {
                case 0:
                    return e1.u;
                case 1:
                    return e2.u;
                default:
                    return e3.u;
            }
        }
    }
    
    public class DistanceComparator implements Comparator<Vertex> {
        private final Vertex compareFrom;
        public DistanceComparator(Vertex from) {
            compareFrom = from;
        }

        @Override
        public int compare(Vertex v1, Vertex v2) {
            double d1 = compareFrom.distance(v1);
            double d2 = compareFrom.distance(v2);

            double diff = d1 - d2;
            if (Math.abs(diff) < EPS) {
                return 0;
            } else {
                return (int) Math.signum(diff);
            }
        }
    }
}
