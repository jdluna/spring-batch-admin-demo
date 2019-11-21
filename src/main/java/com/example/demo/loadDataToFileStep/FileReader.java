package com.example.demo.loadDataToFileStep;

import com.example.demo.model.Transaction;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

public class FileReader extends FlatFileItemReader<Transaction> {

    public FileReader(Resource resource) {
        super();
        setResource(resource);
        setName("Transaction CSV Reader");

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[] { "userName", "userId", "transactionDate", "amount", "deptName" });
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);

        DefaultLineMapper<Transaction> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(new RecordFieldSetMapper());

        setLineMapper(defaultLineMapper);
    }
}
