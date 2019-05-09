package nl.naturalis.geneious.csv;

import static nl.naturalis.geneious.gui.log.GuiLogger.plural;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.WrappedException;

/**
 * A simple reader for the types of files the plugin deal with: CSV files, TSV files and spreadsheet. Not meant to be generic or adaptable,
 * but sufficient for our needs. Reads the entire file into memory. This class will read .txt files, but they will always be presumed to
 * contain tab-delimited columns (i.e. they will be parsed like TSV files).
 *
 * @author Ayco Holleman
 */
public class RowSupplier {

  private final CsvImportConfig<?> cfg;

  public RowSupplier(CsvImportConfig<?> config) {
    this.cfg = config;
  }

  public List<String[]> getDataRows() {
    File file = cfg.getFile();
    List<String[]> rows;
    try {
      if (CsvImportUtil.isSpreadsheet(file.getName())) {
        SpreadSheetReader ssr = new SpreadSheetReader(file);
        ssr.setSheetNumber(cfg.getSheetNumber());
        rows = ssr.readAllRows();
      } else if (CsvImportUtil.isCsvFile(file.getName())) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(cfg.getDelimiter().charAt(0));
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else {
        throw new NaturalisPluginException("Unknown file type");
      }
      return trim(rows, true);
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

  public List<String[]> getAllRows() {
    File file = cfg.getFile();
    List<String[]> rows;
    try {
      if (CsvImportUtil.isSpreadsheet(file.getName())) {
        SpreadSheetReader ssr = new SpreadSheetReader(file);
        ssr.setSheetNumber(cfg.getSheetNumber());
        rows = ssr.readAllRows();
      } else if (CsvImportUtil.isCsvFile(file.getName())) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(cfg.getDelimiter().charAt(0));
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else {
        throw new NaturalisPluginException("Unknown file type");
      }
      return trim(rows, false);
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

  // Removes any trailing whitespace-only rows.
  private List<String[]> trim(List<String[]> rows, boolean skipHeader) {
    int skip = cfg.getSkipLines();
    if (rows.size() != 0) {
      int i;
      for (i = rows.size() - 1; i != 0; --i) {
        if (containsData(rows.get(i))) {
          break;
        }
      }
      if (i <= skip) {
        String msg = String.format("No rows remaining after skipping %d line%s", skip, plural(skip));
        throw new NaturalisPluginException(msg);
      }
      if ((skipHeader && skip != 0) || i != rows.size() - 1) {
        return rows.subList(skip, i + 1);
      }
    }
    return rows;
  }

  private static boolean containsData(String[] row) {
    return Arrays.stream(row).filter(StringUtils::isNotBlank).findFirst().isPresent();
  }

}
