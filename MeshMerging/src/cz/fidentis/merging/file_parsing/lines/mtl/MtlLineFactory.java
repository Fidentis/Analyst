package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class MtlLineFactory {

    private MtlLineFactory() {

    }

    /**
     *
     * @param line
     * @return
     */
    public static MtlLine create(final FileLine line) {

        switch (line.getPrefix()) {
            case PhongSpecularComponentLine.PREFIX:
                return new PhongSpecularComponentLine(line);
            case ColorDifusseLine.PREFIX:
                return new ColorDifusseLine(line);
            case ColorAmbientLine.PREFIX:
                return new ColorAmbientLine(line);
            case ColorSpecularLine.PREFIX:
                return new ColorSpecularLine(line);
            case DisolveFactorLine.PREFIX:
                return new DisolveFactorLine(line);
            case DisolveFactorLine.PREFIX_ALIAS:
                return new DisolveFactorLine(line);
            case RefractionIndexLine.PREFIX:
                return new RefractionIndexLine(line);
            case IlluminationLine.PREFIX:
                return new IlluminationLine(line);
            case TextureNameLine.PREFIX:
                return new TextureNameLine(line);
            case TransmissionFilterLine.PREFIX:
                return new TransmissionFilterLine(line);
            case NewMaterialLine.PREFIX:
                return new NewMaterialLine(line);
            case EmmisionLine.PREFIX:
                return new EmmisionLine(line);
            default:
                return new UnsuportedLine(line);
        }
    }

}
