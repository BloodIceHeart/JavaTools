package word;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;

public class WordMerge {
    public static void main(String[] args) {
        //创建 Document 类的对象并从磁盘加载 Word 文档
        Document document = new Document("F:\\迅雷下载\\1.docx");

        //将另一个文档插入当前文档
        document.insertTextFromFile("F:\\迅雷下载\\2.docx", FileFormat.Docx_2013);

        //保存结果文档
        document.saveToFile("F:\\迅雷下载\\合并结果.docx", FileFormat.Docx_2013);
    }
}
