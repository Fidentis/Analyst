package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public interface MtlLine {

    /**
     *
     * @param sm
     */
    void appendTo(Materials sm);
}
