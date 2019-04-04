package nl.naturalis.geneious.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.WrappedException;

/**
 * A simple reader for the types of files the plugin deal with: CSV files, TSV files and spreadsheet. Not meant to be
 * generic or adaptable, but sufficient for our needs. Reads the entire file into memory. This class will read .txt
 * files, but they will always be presumed to contain tab-delimited columns (i.e. they will be parsed like TSV files).
 *
 * @author Ayco Holleman
 */
public class RowSupplier {

  private final CsvImportConfig<?> cfg;

  public RowSupplier(CsvImportConfig<?> config) {
    this.cfg = config;
  }

  public List<String[]> getAllRows() {
    File file = cfg.getFile();
    List<String[]> rows;
    try {
      if (CsvImportUtil.isSpreadsheet(file.getName())) {
        SpreadSheetReader ssr = new SpreadSheetReader(file);
        ssr.setSheetNumber(cfg.getSheetNumber());
        ssr.setSkipRows(cfg.getSkipLines());
        rows = ssr.readAllRows();
      } else if (CsvImportUtil.isCsvFile(file.getName())) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(cfg.getDelimiter().charAt(0));
        settings.setNumberOfRowsToSkip(cfg.getSkipLines());
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else {
        throw new NaturalisPluginException("Unknown file type");
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
