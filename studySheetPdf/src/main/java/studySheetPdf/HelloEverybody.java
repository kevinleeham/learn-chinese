package studySheetPdf;

import java.io.File;
import java.io.IOException;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class HelloEverybody {

	private Color FONT_COLOR = new DeviceRgb(20,20,20);
	private String FONT_FILE_ENGLISH = "src/main/resources/fonts/english/TidyHand.ttf";
	private String FONT_FILE_CHINESE = "src/main/resources/fonts/chinese/arphic/bkai00mp.ttf";

	public void createPdf(File pdfFile) {
		
		System.out.println(pdfFile.getAbsolutePath());
		
		// create the three fonts we want in our Study Sheet
		boolean embedded = true;
		try {
			PdfFont pdfFontEnglish = PdfFontFactory.createFont(this.FONT_FILE_ENGLISH, embedded);
			PdfFont pdfFontChinese = PdfFontFactory.createFont(this.FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H, embedded);
	
			// create the PDF document
			PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
		    Document document = new Document(pdfDocument, PageSize.LETTER.rotate());

		    // add three sample paragraphs
			document.add(new Paragraph("Hello everybody!").setFont(pdfFontEnglish).setFontSize(22).setFontColor(FONT_COLOR));
			document.add(new Paragraph("大家好！").setFont(pdfFontChinese).setFontSize(22).setFontColor(FONT_COLOR));
	
			// close the PDF document
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		HelloEverybody he = new HelloEverybody();
		he.createPdf(new File("helloEverybody.pdf"));
	}
}