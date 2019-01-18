package dto;

public class ProductDto {

    private long ean;
    private String name;
    private String description;
    private String url;

    public ProductDto() {
        // Default Ctor for Inject
    }

    public ProductDto(long ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public ProductDto(long ean, String name, String description, String url) {
        this(ean, name, description);
        this.url = url;
    }

    public long getEan() {
        return ean;
    }

    public void setEan(long ean) {
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String filePath) {
        this.url = filePath;
    }

    public String getUrl() {
        return url;
    }
}
