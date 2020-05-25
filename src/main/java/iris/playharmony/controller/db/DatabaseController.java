package iris.playharmony.controller.db;

import iris.playharmony.model.Playlist;
import iris.playharmony.model.Song;
import iris.playharmony.model.User;

import javax.xml.crypto.Data;
import java.util.List;

public class DatabaseController implements IUserDatabaseController, ISongDatabaseController, IPlaylistDatabaseController {

    private static DatabaseController instance;

    public static DatabaseController get() {
        if(instance == null) {
            synchronized (DatabaseController.class) {
                instance = new DatabaseController();
            }
        }
        return instance;
    }


    private final IUserDatabaseController userDatabaseController;
    private final ISongDatabaseController songDatabaseController;
    private final IPlaylistDatabaseController playlistDatabaseController;

    public DatabaseController() {
        userDatabaseController = new UserDatabaseController();
        songDatabaseController = new SongDatabaseController();
        playlistDatabaseController = new PlaylistDatabaseController();
    }

    @Override
    public boolean addSong(Song song) {
        return songDatabaseController.addSong(song);
    }

    @Override
    public boolean deleteSong(Song song) {
        return songDatabaseController.deleteSong(song);
    }

    @Override
    public List<Song> getSongs() {
        return songDatabaseController.getSongs();
    }

    @Override
    public List<User> getUsers() {
        return userDatabaseController.getUsers();
    }

    @Override
    public boolean addUser(User user) {
        return userDatabaseController.addUser(user);
    }

    @Override
    public boolean updateUser(User user, String key) {
        return userDatabaseController.updateUser(user, key);
    }

    @Override
    public boolean removeUser(String key) {
        return userDatabaseController.removeUser(key);
    }

    @Override
    public boolean addPlayList(Playlist updatedPlaylist, User user) {
        return playlistDatabaseController.addPlayList(updatedPlaylist, user);
    }

    @Override
    public boolean deletePlayList(Playlist playList, User user) {
        return playlistDatabaseController.deletePlayList(playList, user);
    }

    @Override
    public boolean addToFavourites(Playlist favourites, User user) {
        return playlistDatabaseController.addToFavourites(favourites, user);
    }
}
