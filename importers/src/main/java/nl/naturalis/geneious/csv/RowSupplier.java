package nl.naturalis.geneious.csv;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.WrappedException;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.NonFatalException;

import static nl.naturalis.geneious.csv.CsvImportUtil.isCsvFile;
import static nl.naturalis.geneious.csv.CsvImportUtil.isSpreadsheet;
import static nl.naturalis.geneious.log.GuiLogger.plural;

/**
 * A simple reader for all CSV-like formats suported by the plugin: CSV files, TSV files and spreadsheets.
 *
 * @author Ayco Holleman
 */
public class RowSupplier {

  private final CsvImportConfig<?> config;

  public RowSupplier(CsvImportConfig<?> config) {
    this.config = config;
  }

  /**
   * Returns all rows, including header rows, within the file.
   * 
   * @return
   * @throws NonFatalException 
   */
  public List<String[]> getAllRows() throws NonFatalException {
    File file = config.getFile();
    List<String[]> rows;
    try {
      if (isSpreadsheet(file.getName())) {
        SpreadSheetReader ssr = new SpreadSheetReader(config);
        rows = ssr.readAllRows();
      } else if (isCsvFile(file.getName())) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(config.getDelimiter().charAt(0));
        CsvParser parser = new CsvParser(settings);
        rows = parser.parseAll(file);
      } else { // Shouldn't happen (already checked)
        throw new IllegalStateException("File type check failure");
      }
      return trim(rows);
    } catch(NonFatalException e) {
      throw e;
    } catch (Throwable t) {
      throw new WrappedException(t);
    }
  }

  /**
   * Returns all rows minus the header rows;
   * 
   * @return
   * @throws NonFatalException 
   */
  public List<String[]> getDataRows() throws NonFatalException {
    List<String[]> all = getAllRows();
    if (all.size() == config.getSkipLines()) {
      return Collections.emptyList();
    }
    return all.subList(config.getSkipLines(), all.size());
  }

  // Removes any trailing whitespace-only rows.
  private List<String[]> trim(List<String[]> rows) {
    int skip = config.getSkipLines();
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
