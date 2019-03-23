package nl.naturalis.geneious.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.CsvImportConfig;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.WrappedException;

public class RowProvider {

  private final CsvImportConfig cfg;

  public RowProvider(CsvImportConfig config) {
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
      return removeTrailingEmptyRows(rows);
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

  private static List<String[]> removeTrailingEmptyRows(List<String[]> rows) {
    if (rows.size() != 0) {
      List<String[]> trimmed = new ArrayList<>(rows);
      int i;
      for (i = trimmed.size() - 1; i != 0; --i) {
        if (Arrays.stream(trimmed.get(i)).filter(StringUtils::isNotBlank).findFirst().isPresent()) {
          break;
        }
      }
      if (i != trimmed.size() - 1) {
        trimmed = trimmed.subList(0, i);
        return trimmed;
      }
    }
    return rows;
  }

}
