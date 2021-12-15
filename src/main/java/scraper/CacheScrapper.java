package scraper;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CacheScrapper implements Scraper{
    private Scraper scraper = new DefaultScraper();
    @SneakyThrows
    public Home scrape(String url) {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        Statement statement = connection.createStatement();
        String query = String.format("select count(*) as count from homes where url='%s'",url);
        ResultSet rs = statement.executeQuery(query);
        System.out.println(rs.getInt("count"));

        if(rs.getInt("count") > 0){
            System.out.println("From cache");
            query = String.format("select * from homes where url='%s'",url);
            rs = statement.executeQuery(query);
            return new Home(rs.getInt("price"),rs.getDouble("beds"),rs.getDouble("bathes"),rs.getDouble("garages"));
        }else{
            Home home = scraper.scrape(url);
            System.out.println(home);
            String homeParametersQuery = String.format("insert into homes(url, price, beds, bathes, garages) values('%s', '%d', '%f', '%f', '%f');",
                    url,
                    home.getPrice(),
                    home.getBeds(),
                    home.getBathes(),
                    home.getGarages());
            statement.executeUpdate(homeParametersQuery);
            return home;
        }

    }
}
