package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "WEB_SEARCH")
public class WebSearch extends Model {
    @Id
    @Constraints.Min(10)
    public Long id;

    @Column(columnDefinition = "VARCHAR(1024)")
    public String name;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<URLEntity> urlList;
    private static Finder<Long, WebSearch> find = new Finder<>(WebSearch.class);

    public WebSearch() {
    }

    public WebSearch(String name) {

        this.name = name;
    }

    public static List<WebSearch> findAll() {

        return find.all();
    }

    public static Optional<WebSearch> findByName(String descr) {

        return find.all().stream().filter(ws -> ws.name.toLowerCase().equals(descr.toLowerCase())).findFirst();
    }
}