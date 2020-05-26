package studySheetPdf;

import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.splitting.ISplitCharacters;

public class MpsSplitCharacters implements ISplitCharacters {
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        int charCode = (int)text.get(glyphPos).getUnicode();
        return (charCode == ' ' || charCode == 'ˇ' || charCode == 'ˋ' || charCode == 'ˊ' || charCode == '・');
    }
}