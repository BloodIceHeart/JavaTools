package word;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PPTtoPDFConverter {

    public static void main(String[] args) {
        try {
            String inputFile = "F:\\迅雷下载\\规则引擎.ppt";
            String outputFile = "F:\\迅雷下载\\规则引擎.pdf";
            //load any ppt file
            FileInputStream inputStream = new FileInputStream(inputFile);
            HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
            inputStream.close();
            Dimension pgsize = ppt.getPageSize();

            //take first slide and save it as an image
            HSLFSlide slide = ppt.getSlides().get(0);;
            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
            slide.draw(graphics);
            FileOutputStream out = new FileOutputStream("F:/slideImage.png");
            javax.imageio.ImageIO.write(img, "png", out);
            out.close();
            
            //get saved slide-image and save it into pdf
            Image slideImage = Image.getInstance("F:/slideImage.png");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.setPageSize(new Rectangle(slideImage.getWidth(), slideImage.getHeight()));
            document.open();
            slideImage.setAbsolutePosition(0, 0);
            document.add(slideImage);
            document.newPage();
            document.add(slideImage);
            document.close();
            
            // 清除临时文件
            File file = new File("F:/slideImage.png");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
