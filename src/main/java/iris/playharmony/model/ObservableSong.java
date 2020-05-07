package iris.playharmony.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ObservableSong {
    private ImageView photo;
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty author = new SimpleStringProperty();
    private SimpleStringProperty date = new SimpleStringProperty();

    public ObservableSong photo(File photo) {
        this.photo = new ImageView(new Image(photo.toURI().toString(), 100, 100, false, false));
        return this;
    }

    public ObservableSong title(String title) {
        this.title.set(title);
        return this;
    }

    public ObservableSong author(String author) {
        this.author.set(author);
        return this;
    }

    public ObservableSong date(String date) {
        this.date.set(date);
        return this;
    }

    public ImageView photo() {
        return photo;
    }

    public SimpleStringProperty title() {
        return title;
    }

    public SimpleStringProperty author() {
        return author;
    }

    public SimpleStringProperty date() {
        return date;
    }

    public static ObservableSong from(Song song) {
        return new ObservableSong()
                .title(song.getTitle())
                .author(song.getAuthor())
                .date(song.getDate())
                .photo(song.getPhoto());
    }
}