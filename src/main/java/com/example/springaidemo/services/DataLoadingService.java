package com.example.springaidemo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DataLoadingService {
    private static final Logger logger = LoggerFactory.getLogger(DataLoadingService.class);

    @Value("classpath:/data/IL_medicaid_small.pdf")
    private Resource pdfResource;

    @Value("classpath:/data/IL_medicaid.json")
    private Resource jsonResource;
    private final VectorStore vectorStore;
    public DataLoadingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
   public void load() {
       PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
               this.pdfResource,
               PdfDocumentReaderConfig.builder()
                       .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                               .withNumberOfBottomTextLinesToDelete(3)
                               .withNumberOfTopPagesToSkipBeforeDelete(1)
                               .build())
                       .withPagesPerDocument(1)
                       .build());

       var textSplitter = new TokenTextSplitter();

       logger.info("Parsing document, splitting, creating embeddings and storing in vector store...  this will take a while.");
       this.vectorStore.accept(
               textSplitter.apply(
                       pdfReader.get()));
       logger.info("Done parsing document, splitting, creating embeddings and storing in vector store");
   }

    public void loadDocument(String document) {
        logger.info("loading in document of ", document);
        vectorStore.add(List.of(new Document(document)));
        logger.info("Document is successfully loaded");
    }

    public void loadJson() {

        JsonReader jsonReader = new JsonReader(jsonResource, new JsonMetadataGeneratorMedicaid(), "Text");
        List<Document> result= jsonReader.get();

        System.out.println("done reading");
        for(Document document : result) {
            try {
                vectorStore.add(Arrays.asList(document));
            } catch (NullPointerException e){
                System.out.println(document);
            }
        }
    }
}
