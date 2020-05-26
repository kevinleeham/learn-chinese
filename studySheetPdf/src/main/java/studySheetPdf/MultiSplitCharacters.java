package studySheetPdf;

import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.splitting.ISplitCharacters;

public class MultiSplitCharacters implements ISplitCharacters {
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        int charCode = (int)text.get(glyphPos).getUnicode();
        return false;
    }
}