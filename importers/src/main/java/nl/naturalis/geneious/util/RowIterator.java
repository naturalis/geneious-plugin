package nl.naturalis.geneious.util;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import org.apache.commons.io.FilenameUtils;

import nl.naturalis.geneious.CsvImportConfig;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.WrappedException;

public class RowIterator implements Iterable<String[]> {

  private final CsvImportConfig cfg;

  public RowIterator(CsvImportConfig config) {
    this.cfg = config;
  }
  
  public List<String[]> getAllRows() {
    File file = cfg.getFile();
    String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
    List<String[]> rows;
    try {
      if (ext.equals("xls") || ext.equals(".xlsx")) {
        SpreadSheetReader ssr = new SpreadSheetReader(file);
        ssr.setSheetNumber(cfg.getSheetNumber());
        ssr.setSkipRows(cfg.getSkipLines());
        rows = ssr.readAllRows();
      } else if (ext.equals(".csv")) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(cfg.getSkipLines());
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else if (ext.equals("tsv") || ext.equals("txt")) {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(cfg.getSkipLines());
        TsvParser parser = new TsvParser(settings);
        rows = parser.parseAll(file);
      } else {
        throw new NaturalisPluginException("Unknown file type: *." + ext);
      }
      return rows;
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
    
  }

  @Override
  public Iterator<String[]> iterator() {
    File file = cfg.getFile();
    String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
    List<String[]> rows;
    try {
      if (ext.equals("xls") || ext.equals(".xlsx")) {
        SpreadSheetReader ssr = new SpreadSheetReader(file);
        ssr.setSheetNumber(cfg.getSheetNumber());
        ssr.setSkipRows(cfg.getSkipLines());
        rows = ssr.readAllRows();
      } else if (ext.equals(".csv")) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(cfg.getSkipLines());
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else if (ext.equals("tsv") || ext.equals("txt")) {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(cfg.getSkipLines());
        TsvParser parser = new TsvParser(settings);
        rows = parser.parseAll(file);
      } else {
        throw new NaturalisPluginException("Unknown file type: *." + ext);
      }
      return rows.iterator();
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

}
