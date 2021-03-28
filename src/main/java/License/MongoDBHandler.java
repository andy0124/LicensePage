package License;

import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.apache.poi.ss.usermodel.*;
import org.bson.Document;

import java.io.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;



//Excel
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;





//Todo 싱글톤으로 만들기
public class MongoDBHandler {

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;


    public MongoDBHandler(){
        mongoClient = new MongoClient( "localhost" , 27017 );
        database = mongoClient.getDatabase("TestLicensePage");
        collection = database.getCollection("IssuedLicense");
    }


    //Insert One Document
    public void insert_doc(String issuedTime, String product, String endDate, String issuedPerson, String siteName, String hardwareID, String licensekey){
        //Create a Document
        Document doc = new Document("issuedTime", issuedTime)
                .append("product", product)
                .append("endDate",endDate)
                .append("issuedPerson", issuedPerson)
                .append("siteName", siteName)
                .append("hardwareID", hardwareID)
                .append("licensekey",licensekey);

        collection.insertOne(doc);
    }


    public List<Document> get_all_doc(){
        List<Document> doc_list = new ArrayList<>();
        for(Document doc : collection.find()){
            doc_list.add(doc);
        }
        Collections.reverse(doc_list);

        return  doc_list;
    }

    public XSSFWorkbook downloadExcel() throws FileNotFoundException,IOException{

        //.xlsx 확장자 지원
        XSSFWorkbook xssfWb = new XSSFWorkbook();
        Sheet sheet1 = xssfWb.createSheet("라이센스 발급 기록");
        Row row =null;
        Cell cell = null;

        int rowIdx = 0;

        row = sheet1.createRow(rowIdx++);

        String[] title = {"발급 신청일","제품명", "만료일", "발급 신청인", "사이트 이름", "하드웨어 아이디", "라이센스 키"};
        int[] cell_width = {5573, 4458, 3821, 4140, 6847, 24601, 24601};
        for(int i=0; i<title.length; i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }
        for(int i=0 ; i< cell_width.length;i++){
            sheet1.setColumnWidth(i,cell_width[i]);
        }

        List<Document> documents = get_all_doc();

        for(int i=0; i<documents.size();i++){
            row = sheet1.createRow(rowIdx++);

            int cellIdx=0;

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("issuedTime").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("product").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("endDate").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("issuedPerson").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("siteName").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("hardwareID").toString());

            cell = row.createCell(cellIdx++);
            cell.setCellValue(documents.get(i).get("licensekey").toString());
        }

        return xssfWb;
    }

/*    public static void main(String[] args) throws IOException {
        MongoDBHandler dd = new MongoDBHandler();
        dd.downloadExcel();
    }*/

}
