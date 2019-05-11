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
 * A simple reader for all types of delimited formats suported by the plugin: CSV files, TSV files and spreadsheet.
 *
 * @author Ayco Holleman
 */
public class RowSupplier {

  private final CsvImportConfig<?> cfg;

  public RowSupplier(CsvImportConfig<?> config) {
    this.cfg = config;
  }

  /**
   * Returns all rows, including header rows, within the file.
   * 
   * @return
   */
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
      return trim(rows);
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

  // Removes any trailing whitespace-only rows.
  private List<String[]> trim(List<String[]> rows) {
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
      if (i != rows.size() - 1) {
        return rows.subList(skip, i + 1);
      }
    }
    return rows;
  }

  private static boolean containsData(String[] row) {
    return Arrays.stream(row).filter(StringUtils::isNotBlank).findFirst().isPresent();
  }

}
