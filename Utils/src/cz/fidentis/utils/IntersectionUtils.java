/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katka
 */
public class IntersectionUtils {

    public static LinkedList<Vector3f> temp;

    private static LinkedList<LinkedList<Vector3f>> connectLists(LinkedList<LinkedList<Vector3f>> tempLists, float threshold) {
        int i = 0;
        while (i <= tempLists.size()) {
            i++;
            boolean next = true;
            while (next == true && i <= tempLists.size()) {
                next = false;
                Vector3f first = new Vector3f(tempLists.get(i - 1).get(0));
                Vector3f last = new Vector3f(tempLists.get(i - 1).get(tempLists.get(i - 1).size() - 1));

                int nearest = 0;
                float nearedDistance = Float.MAX_VALUE;
                for (int j = 0; j < tempLists.size(); j++) {
                    if ((i - 1) != j) {
                        Vector3f firstJ = new Vector3f(tempLists.get(j).get(0));
                        Vector3f lastJ = new Vector3f(tempLists.get(j).get(tempLists.get(j).size() - 1));

                        float firstFirst = (float) MathUtils.instance().distancePoints(first, firstJ);
                        float firstLast = (float) MathUtils.instance().distancePoints(first, lastJ);
                        float lastFirst = (float) MathUtils.instance().distancePoints(last, firstJ);
                        float lastLast = (float) MathUtils.instance().distancePoints(last, lastJ);

                        float[] dists = {firstFirst, firstLast, lastFirst, lastLast};
                        int min = 0;
                        float minDist = firstFirst;
                        for (int k = 1; k < 4; k++) {
                            if (dists[k] < minDist) {
                                min = k;
                                minDist = dists[k];
                            }
                        }
                        if (minDist < nearedDistance) {
                            nearest = j;
                            nearedDistance = minDist;
                        }
                    }
                }
                if (i - 1 != nearest) {

                    Vector3f firstJ = new Vector3f(tempLists.get(nearest).get(0));
                    Vector3f lastJ = new Vector3f(tempLists.get(nearest).get(tempLists.get(nearest).size() - 1));

                    float firstFirst = (float) MathUtils.instance().distancePoints(first, firstJ);
                    float firstLast = (float) MathUtils.instance().distancePoints(first, lastJ);
                    float lastFirst = (float) MathUtils.instance().distancePoints(last, firstJ);
                    float lastLast = (float) MathUtils.instance().distancePoints(last, lastJ);

                    float[] dists = {firstFirst, firstLast, lastFirst, lastLast};
                    int min = 0;
                    for (int k = 1; k < 4; k++) {
                        if (dists[k] < dists[min]) {
                            min = k;
                        }
                    }

                    if (min == 0 && firstFirst < threshold) {
                        for (Vector3f v : tempLists.get(nearest)) {
                            tempLists.get(i - 1).addFirst(v);
                        }
                        tempLists.remove(nearest);
                        next = true;

                    }

                    if (min == 3 && lastLast < threshold) {
                        while (tempLists.get(nearest).size() > 0) {
                            tempLists.get(i - 1).addLast(tempLists.get(nearest).pollLast());
                        }
                        tempLists.remove(nearest);
                        next = true;

                    }

                    if (min == 1 && firstLast < threshold) {
                        tempLists.get(i - 1).addAll(0, tempLists.get(nearest));
                        tempLists.remove(nearest);
                        next = true;

                    }

                    if (min == 2 && lastFirst < threshold) {
                        tempLists.get(i - 1).addAll(tempLists.get(nearest));
                        tempLists.remove(nearest);
                        next = true;

                    }

                }
            }
        }
        return tempLists;
    }

    private static LinkedList<LinkedList<Vector3f>> connectPoints(LinkedList<Vector3f> a, LinkedList<Vector3f> b) {
        LinkedList<LinkedList<Vector3f>> tempLists = new LinkedList<>();
        while (!a.isEmpty()) {
            LinkedList<Vector3f> c = new LinkedList<>();
            c.add(a.get(0));
            a.remove(0);
            c.add(b.get(0));
            b.remove(0);
            boolean next = true;
            while (next == true) {
                next = false;
                for (int i = 0; i < a.size(); i++) {
                    if (a.get(i).equals(c.get(c.size() - 1))) {
                        if (!b.get(i).equals(c.get(c.size() - 2))) {
                            c.add(b.get(i));
                        }
                        a.remove(i);
                        b.remove(i);
                        next = true;
                        break;
                    }
                    if (b.get(i).equals(c.get(c.size() - 1))) {
                        if (!a.get(i).equals(c.get(c.size() - 2))) {
                            c.add(a.get(i));
                        }
                        a.remove(i);
                        b.remove(i);
                        next = true;
                        break;
                    }
                    if (a.get(i).equals(c.get(0))) {
                        if (!b.get(i).equals(c.get(1))) {
                            c.add(0, b.get(i));
                        }
                        a.remove(i);
                        b.remove(i);
                        next = true;
                        break;
                    }
                    if (b.get(i).equals(c.get(0))) {
                        if (!a.get(i).equals(c.get(1))) {
                            c.add(0, a.get(i));
                        }
                        a.remove(i);
                        b.remove(i);
                        next = true;
                        break;
                    }
                }
            }
            tempLists.add(c);
        }
        return tempLists;
    }

    public static LinkedList<LinkedList<Vector3f>> findModelPlaneIntersection(Model model, Vector3f planeNormal, Vector3f planePoint) {
        LinkedList<Vector3f> a = new LinkedList<>();
        LinkedList<Vector3f> b = new LinkedList<>();
        for (int i = 0; i < model.getFaces().getNumFaces(); i++) {
            int idx[] = model.getFaces().getFaceVertIdxs(i);
            Vector3f triangle[] = {model.getVerts().get(idx[0] - 1), model.getVerts().get(idx[1] - 1), model.getVerts().get(idx[2] - 1)};
            findTrianglePlaneIntersection(a, b, triangle, planeNormal, planePoint);
        }
        return connectLists(connectPoints(a, b), 0.1f);
    }

    public static float distanceBetweenVertices(Model m, int aIndex, int bIndex) {
        Vector3f a = m.getVerts().get(aIndex);
        Vector3f b = m.getVerts().get(bIndex);
        Vector3f abVector = new Vector3f(b);
        abVector.sub(a);
        abVector.normalize();

        Vector3f n = new Vector3f(m.getVertexNormal(aIndex));
        n.normalize();

        Vector3f planeNormal = new Vector3f();
        planeNormal.cross(n, abVector);
        planeNormal.normalize();

        float finalDistance = 0;
        int nextA = aIndex;
        int nextB = -1;
        int control = 0;
        Vector3f lastIntersection = new Vector3f(a);

        Set<Integer> facesTmp = new HashSet<>();

        LinkedList<Vector3f> av = new LinkedList<>();
        LinkedList<Vector3f> bv = new LinkedList<>();
        for (int i = 0; i < m.getFaces().getNumFaces(); i++) {
            int idx[] = m.getFaces().getFaceVertIdxs(i);
            Vector3f triangle[] = {m.getVerts().get(idx[0] - 1), m.getVerts().get(idx[1] - 1), m.getVerts().get(idx[2] - 1)};
            if (idx[0] - 1 == aIndex || idx[1] - 1 == aIndex || idx[2] - 1 == aIndex) {
                findTrianglePlaneIntersection(av, bv, triangle, planeNormal, a);
                facesTmp.add(i);
            }

        }
        LinkedList<LinkedList<Vector3f>> lists = connectLists(connectPoints(av, bv), 10f);
        if (lists.getFirst().lastIndexOf(a) != 0 && lists.getFirst().lastIndexOf(a) != lists.getFirst().size() - 1) {
            finalDistance = (float) (MathUtils.instance().distancePoints(lists.getFirst().getFirst(), b) < MathUtils.instance().distancePoints(lists.getFirst().getLast(), b) ? MathUtils.instance().distancePoints(lists.getFirst().getFirst(), a) : MathUtils.instance().distancePoints(lists.getFirst().getLast(), a));
        } else if (lists.getFirst().lastIndexOf(a) == 0) {
            finalDistance = (float) MathUtils.instance().distancePoints(lists.getFirst().getLast(), a);
        } else {
            finalDistance = (float) MathUtils.instance().distancePoints(lists.getFirst().getFirst(), a);
        }

        while (!lastIntersection.equals(b) && control < 50) {
            control++;
            av = new LinkedList<>();
            bv = new LinkedList<>();
            for (int i = 0; i < m.getFaces().getNumFaces(); i++) {
                int idx[] = m.getFaces().getFaceVertIdxs(i);
                Vector3f triangle[] = {m.getVerts().get(idx[0] - 1), m.getVerts().get(idx[1] - 1), m.getVerts().get(idx[2] - 1)};

                boolean connected = false;
                for (Integer j: facesTmp) {
                    int idxj[] = m.getFaces().getFaceVertIdxs(j);                    
                    for (int k = 0; k < 3; k++) {
                        if (idx[0] == idxj[k] || idx[1] == idxj[k] || idx[2] == idxj[k]) {
                            connected = true;
                        }
                    }
                }
                if (connected) {
                    findTrianglePlaneIntersection(av, bv, triangle, planeNormal, a);
                    facesTmp.add(i);
                }

            }
            lists = connectLists(connectPoints(av, bv), 10f);
            if (lists.getFirst().lastIndexOf(a) != 0 && lists.getFirst().lastIndexOf(a) != lists.getFirst().size() - 1) {
                finalDistance += MathUtils.instance().distancePoints(lists.getFirst().getFirst(), b) < MathUtils.instance().distancePoints(lists.getFirst().getLast(), b) ? MathUtils.instance().distancePoints(lists.getFirst().getFirst(), lastIntersection) : MathUtils.instance().distancePoints(lists.getFirst().getLast(), lastIntersection);
                lastIntersection = MathUtils.instance().distancePoints(lists.getFirst().getFirst(), b) < MathUtils.instance().distancePoints(lists.getFirst().getLast(), b) ? lists.getFirst().getFirst() : lists.getFirst().getLast();
            } else if (lists.getFirst().lastIndexOf(a) == 0) {
                finalDistance = (float) MathUtils.instance().distancePoints(lists.getFirst().getLast(), lastIntersection);
                lastIntersection = lists.getFirst().getLast();
            } else {
                finalDistance = (float) MathUtils.instance().distancePoints(lists.getFirst().getFirst(), lastIntersection);
                lastIntersection = lists.getFirst().getFirst();
            }

        }
        /*
        
        
         while (!lastIntersection.equals(b) && control < 50) {
         control++;
         ArrayList<Integer> edgesTmp = new ArrayList<>();
         ArrayList<Vector3f> intersectionsTmp = new ArrayList<>();
         adherentFaces(m, nextA, nextB, intersectionsTmp, facesTmp, edgesTmp, planeNormal, a);
         if (intersectionsTmp.size() > 0) {
         float dist = Float.MAX_VALUE;
         int inters = 0;

         for (int i = 0; i < intersectionsTmp.size(); i++) {
         if (getDistance(b, intersectionsTmp.get(i)) < dist) {
         dist = getDistance(b, intersectionsTmp.get(i));
         inters = i;
         }
         }
         finalDistance += getDistance(lastIntersection, intersectionsTmp.get(inters));
         lastIntersection = intersectionsTmp.get(inters);
         nextA = edgesTmp.get(inters * 2);
         nextB = edgesTmp.get(inters * 2 + 1);
         }
         }*/
        return finalDistance;

    }

    private static void adherentFaces(Model m, int nextA, int nextB, ArrayList<Vector3f> intersections, ArrayList<Integer> faces, ArrayList<Integer> edges, Vector3f planeNormal, Vector3f p) {
        System.out.println("next a: " + nextA + "next b: " + nextB);

        for (int i = 0; i < m.getFaces().getNumFaces(); i++) {
            if (!faces.contains(i)) {
                int[] verts = m.getFaces().getFaceVertIdxs(i);

                for (int j = 0; j < verts.length; j++) {
                    int k = j + 1 > 2 ? j - 2 : j + 1;
                    int l = j + 2 > 2 ? j - 1 : j + 2;
                    if ((nextB == -1 && verts[j] - 1 == nextA) || (verts[j] - 1 == nextA && verts[k] - 1 == nextB) || (verts[k] - 1 == nextA && verts[j] - 1 == nextB)) {
                        Vector3f in, in1, in2;
                        System.out.println(verts[j] + "; " + verts[k] + "; " + verts[l] + "; ");
                        if (nextB == -1 && verts[j] == nextA) {
                            in = findSegmentPlaneIntersection(m.getVerts().get(verts[k] - 1), m.getVerts().get(verts[j] - 1), planeNormal, p);
                            in1 = findSegmentPlaneIntersection(m.getVerts().get(verts[l] - 1), m.getVerts().get(verts[j] - 1), planeNormal, p);
                            in2 = findSegmentPlaneIntersection(m.getVerts().get(verts[k] - 1), m.getVerts().get(verts[l] - 1), planeNormal, p);
                        } else {
                            in = null;
                            in1 = findSegmentPlaneIntersection(m.getVerts().get(verts[l] - 1), m.getVerts().get(verts[j] - 1), planeNormal, p);
                            in2 = findSegmentPlaneIntersection(m.getVerts().get(verts[k] - 1), m.getVerts().get(verts[l] - 1), planeNormal, p);
                        }

                        if (in != null) {
                            intersections.add(in);
                            faces.add(i);
                            if (in.equals(m.getVerts().get(verts[k] - 1))) {
                                edges.add(verts[k] - 1);
                                edges.add(-1);
                            } else {
                                edges.add(verts[k] - 1);
                                edges.add(j);
                            }
                        } else if (in1 != null) {
                            intersections.add(in1);
                            faces.add(i);
                            if (in1.equals(m.getVerts().get(verts[l] - 1))) {
                                edges.add(verts[l] - 1);
                                edges.add(-1);
                            } else {
                                edges.add(verts[l] - 1);
                                edges.add(verts[j] - 1);
                            }
                        } else if (in2 != null) {
                            intersections.add(in2);
                            faces.add(i);
                            edges.add(verts[k] - 1);
                            edges.add(verts[l] - 1);
                        }
                        System.out.println("ints: " + intersections.toString());

                    }
                }
            }
        }
    }

    /**
     * Computes intersection of model and plane. Then finds distance of given
     * points along this intersection.
     *
     * @param model model
     * @param planeNormal normal of the plane, the plane should contain points
     * pA and pB
     * @param planePoint point from the plane, the plane should contain points
     * pA and pB
     * @param pA angle around horizontal axe on screen
     * @param pB angle around horizontal axe on screen
     * @return distance of points pA and pB along the surface intersection with
     * given plane
     */
    public static float findSurfaceDistanceBetweenPoints(Model model, Vector3f planeNormal, Vector3f planePoint, Vector3f pA, Vector3f pB, float threshold,Set<Integer> faces) {
        ArrayList<Integer> tmpFaces = new ArrayList<>();
        LinkedList<Vector3f> a = new LinkedList<>();
        LinkedList<Vector3f> b = new LinkedList<>();
        int size = 0;
        for (int i = 0; i < model.getFaces().getNumFaces(); i++) {
            int idx[] = model.getFaces().getFaceVertIdxs(i);
            Vector3f triangle[] = {model.getVerts().get(idx[0] - 1), model.getVerts().get(idx[1] - 1), model.getVerts().get(idx[2] - 1)};
            findTrianglePlaneIntersection(a, b, triangle, planeNormal, planePoint);
            if(a.size()>size){
                tmpFaces.add(i);
                size++;
            }
            
        }

        LinkedList<LinkedList<Vector3f>> lists = connectLists(connectPoints(new LinkedList<>(a), new LinkedList<>(b)), 10f);
        float distance = Float.MAX_VALUE;
        for (LinkedList<Vector3f> list : lists) {
          //  if (getDistance(list.getLast(), list.getFirst()) < 0.5f) {
          //      list.addFirst(list.getLast());
          //  }
            float dist_temp = 0;
            if (list.contains(pB) && list.contains(pA)) {
                temp = list;
                int aIndex = list.indexOf(pA);
                int bIndex = list.indexOf(pB);
                int min = aIndex < bIndex ? aIndex : bIndex;
                int max = aIndex > bIndex ? aIndex : bIndex;

                for (int i = min; i < max; i++) {
                    Vector3f u = new Vector3f(list.get(i));
                    if(a.contains(u)){
                        faces.add(tmpFaces.get(a.indexOf(u)));
                        faces.add(tmpFaces.get(a.lastIndexOf(u)));
                    }
                    if(b.contains(u)){
                        faces.add(tmpFaces.get(b.indexOf(u)));
                        faces.add(tmpFaces.get(b.lastIndexOf(u)));
                    }
                    
                    
                    u.sub(list.get(i + 1));
                    dist_temp += u.length();
                }

                if (dist_temp < distance) {
                    distance = dist_temp;
                }
            }

        }

        /*
         int pAListIndex = -1, pAindex = -1, pBListIndex = -1, pBindex = -1;
         float pAdist = Float.MAX_VALUE, pBdist = Float.MAX_VALUE;
         //setting treshold for connecting to max value should esure that all list are conected into one;
         LinkedList<LinkedList<Vector3f>> lists = connectLists(connectPoints(a, b), Float.MAX_VALUE);
         for (int i = 0; i < lists.size(); i++) {
         for (int j = 0; j < lists.get(i).size(); j++) {
         Vector3f pToPA = new Vector3f(pA);
         pToPA.sub(lists.get(i).get(j));
         Vector3f pToPB = new Vector3f(pB);
         pToPB.sub(lists.get(i).get(j));
         if (pToPA.length() < pAdist) {
         pAListIndex = i;
         pAindex = j;
         pAdist = pToPA.length();
         }
         if (pToPB.length() < pBdist) {
         pBListIndex = i;
         pBindex = j;
         pBdist = pToPB.length();
         }

         }
         }

         float distance = 0;
         if (pAListIndex == pBListIndex) {
         int min = pAindex < pBindex ? pAindex : pBindex;
         int max = pAindex > pBindex ? pAindex : pBindex;
         for (int i = min; i < max; i++) {
         Vector3f u = new Vector3f(lists.get(pBListIndex).get(i));
         u.sub(lists.get(pBListIndex).get(i + 1));
         System.out.println("u" + u.length());
         distance += u.length();
         }

         return distance;
         }
         return -1;*/
        return distance;
    }

    /*public static float getDistance(Vector3f a, Vector3f b) {
        Vector3f u = new Vector3f(a);
        u.sub(b);
        return u.length();
    }*/

    private static void findTrianglePlaneIntersection(LinkedList<Vector3f> a, LinkedList<Vector3f> b, Vector3f[] triangle, Vector3f n, Vector3f p) {
        Vector3f in = findSegmentPlaneIntersection(triangle[0], triangle[1], n, p);
        Vector3f in1 = findSegmentPlaneIntersection(triangle[1], triangle[2], n, p);
        Vector3f in2 = findSegmentPlaneIntersection(triangle[2], triangle[0], n, p);
        if (in == null && in1 == null && in2 == null) {
            return;
        } else if (in != null && in1 != null && in2 == null) {
            if (in != in1) {
                a.add(in);
                b.add(in1);
            }
        } else if (in == null && in1 != null && in2 != null) {
            if (in2 != in1) {
                a.add(in1);
                b.add(in2);
            }
        } else if (in != null && in1 == null && in2 != null) {
            if (in2 != in) {
                a.add(in2);
                b.add(in);
            }
        } else if (in != null && in1 != null && in2 != null) {
            if (in == in1) {
                a.add(in);
                b.add(in2);
            } else if (in1 == in2) {
                a.add(in);
                b.add(in2);
            } else {
                a.add(in1);
                b.add(in2);
            }
        }

    }

    /**
     *
     * @param pl line point
     * @param u line vector
     * @param n plane normal
     * @param p point from plane
     * @return intersection of line and plane, if it doesn't exist returns null
     */
    public static Vector3f findLinePlaneIntersection(Vector3f pl, Vector3f u, Vector3f n, Vector3f p) {
        Vector3f w = new Vector3f(pl);
        w.sub(p);

        float D = n.dot(u);
        float N = -n.dot(w);

        if (Math.abs(D) == 0) {   // segment is parallel to plane
            if (N == 0) // segment lies in plane
            {
                return pl;
            } else {
                return null;                    // no intersection
            }
        }
        // they are not parallel
        // compute intersect param
        float sI = N / D;

        Vector3f intersection = new Vector3f(u);
        intersection.scale(sI);
        intersection.add(pl);

        return intersection;
    }

    /**
     *
     * @param p0 segment point
     * @param p1 segment point
     * @param n plane normal
     * @param p point from plane
     * @return intersection of segment and plane, if it doesn't exist returns
     * null
     */
    public static Vector3f findSegmentPlaneIntersection(Vector3f p0, Vector3f p1, Vector3f n, Vector3f p) {
        Vector3f u = new Vector3f(p1);
        u.sub(p0);
        Vector3f w = new Vector3f(p0);
        w.sub(p);

        float D = n.dot(u);
        float N = -n.dot(w);

        if (Math.abs(D) == 0) {   // segment is parallel to plane
            if (N == 0) // segment lies in plane
            {
                return p0;
            } else {
                return null;                    // no intersection
            }
        }
        // they are not parallel
        // compute intersect param
        float sI = N / D;
        if (sI < 0 || sI > 1) {
            // if (t < 0 || t> 1) {
            return null;                        // no intersection
        }
        Vector3f intersection = new Vector3f(u);
        intersection.scale(sI);
        intersection.add(p0);

        return intersection;
    }

    /**
     *
     * @param triangle triangle for intersection calculation.
     * @param p point to be tested
     * @return true if point lies in triangle
     */
    public static boolean pointInTriangle(Vector3f[] triangle, Vector3f p) {
        if (triangle.length == 3) {
            Vector3f u = new Vector3f(triangle[1].getX() - triangle[0].getX(), //T1-T0
                    triangle[1].getY() - triangle[0].getY(),
                    triangle[1].getZ() - triangle[0].getZ());
            Vector3f v = new Vector3f(triangle[2].getX() - triangle[0].getX(),//T2-T0
                    triangle[2].getY() - triangle[0].getY(),
                    triangle[2].getZ() - triangle[0].getZ());

            Vector3f w = new Vector3f(p.getX() - triangle[0].getX(), //PI-T0
                    p.getY() - triangle[0].getY(),
                    p.getZ() - triangle[0].getZ());

            float uv = u.dot(v);
            float uu = u.dot(u);
            float vv = v.dot(v);
            float wu = w.dot(u);
            float wv = w.dot(v);
            float s = (uv * wv - vv * wu) / (uv * uv - uu * vv);
            float t = (uv * wu - uu * wv) / (uv * uv - uu * vv);

            if (s >= 0 && t >= 0 && s + t <= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find projection of the point to triangle edges. Returned projection is the one with smallest distance to original point p.
     * If AB is the edge of triangle, projected point might not be on edge AB, but rather on ray AB.
     * @param p - point to project
     * @param a - point of triangle
     * @param b - point of triangle
     * @param c - point of triangle
     * @return projection of point p to edges AB, BC and CA, which has smallest distance to original point
     */
    public static Vector3f projectionToTriangleEdges(Vector3f p, Vector3f a, Vector3f b, Vector3f c) {
        Vector3f[] projections = new Vector3f[3];
        projections[0] = projectionToTriangleEdge(p, a, b);
        projections[1] = projectionToTriangleEdge(p, b, c);
        projections[2] = projectionToTriangleEdge(p, c, a);
        Vector3f closest = projections[0];
        float minDist = (float) MathUtils.instance().distancePoints(closest, p);
        for (int i = 1; i < 3; i++) {
            float dist = (float) MathUtils.instance().distancePoints(projections[i], p);
            if (dist < minDist) {
                minDist = dist;
                closest = projections[i];
            }
        }
        return closest;
    }

    //projects point p to single edge AB
    private static Vector3f projectionToTriangleEdge(Vector3f p, Vector3f a, Vector3f b) {
        Vector3f ab = MathUtils.instance().createVector(a, b);
        Vector3f ap = MathUtils.instance().createVector(a, p);
        float t = ab.dot(ap) / ab.dot(ab);
        return new Vector3f(a.x + t * ab.x, a.y + t * ab.y, a.z + t * ab.z);
    }

}
