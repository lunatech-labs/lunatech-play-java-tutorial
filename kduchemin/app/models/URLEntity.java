package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "URL_ENTITY")
public class URLEntity extends Model {

    @Id
    @Column(name = "url_entity_number")
    @Constraints.Min(10)
    public Long id;
    @Column(columnDefinition = "VARCHAR(1024)")
    public String url;

    @ManyToMany
    public List<WebSearch> webSearch = new ArrayList<>();

    private static Finder<Long, URLEntity> find = new Finder<>(URLEntity.class);

    public URLEntity() {
    }

    public static List<URLEntity> findAll() {

        return find.all();
    }


    public URLEntity(String url) {

        this.url = Objects.requireNonNull(url);
    }
}