package input;

import bollinger.PriceData;
import org.joda.time.LocalDate;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vince on 11/04/15.
 */
public class InputReader{

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    private Path path;

    public InputReader(String filename) throws URISyntaxException {
        URL url = getClass().getResource(filename);
        this.path = Paths.get(url.toURI());
    }

    public List<PriceData> getPriceInput() throws Exception{

        try(Stream<String> lines = Files.lines(path)){
            Stream<PriceData> str = lines.skip(1).map(line -> {

                String[] row = line.split(",");
                return parseRow(row);

            }).filter(Optional::isPresent).map(Optional::get);

            return str.collect(Collectors.toList() );
        }
    }

    private Optional<PriceData> parseRow(String[] row) {
        try{
            PriceData pd = new PriceData(
                    LocalDate.fromDateFields(format.parse(row[0])),
                    Double.parseDouble(row[1]),
                    Double.parseDouble(row[2]),
                    Double.parseDouble(row[3]),
                    Double.parseDouble(row[4])
            );

            return Optional.of(pd);
        } catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }

    }

}
