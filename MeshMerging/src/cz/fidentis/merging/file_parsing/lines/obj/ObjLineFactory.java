package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class ObjLineFactory {

    private ObjLineFactory() {

    }

    /**
     *
     * @param line
     * @return
     */
    public static ObjLine getObjLine(final FileLine line) {
        switch (line.getPrefixStart()) {
            case 'v':
                return getCoordsLine(line);
            case 'g':
                return new Group(line);
            case 's':
                return new Shading(line);
            case 'f':
                return new Index(line);
            default:
                break;
        }

        switch (line.getPrefix()) {
            case "mtllib":
                return new MtlFile(line);
            case "usemtl":
                return new UseMtl(line);
            default:

                return new UnsupportedLine(line);
        }

    }

    private static ObjLine getCoordsLine(final FileLine line) {
        switch (line.getPrefix()) {
            case "v":
                return new Vertex(line);
            case "vn":
                return new Normal(line);
            case "vt":
                return new TextureCoord(line);
            default:
                return new UnsupportedLine(line);
        }
    }
}
