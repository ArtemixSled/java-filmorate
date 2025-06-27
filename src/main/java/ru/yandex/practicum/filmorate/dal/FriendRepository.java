package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendRepository extends BaseRepository<User> implements FriendStorage {

    public FriendRepository(JdbcTemplate jdbc) {
        super(jdbc, User.class);
    }

    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO user_friends(user_id, friend_id) VALUES(?, ?)";
        jdbc.update(sql, userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
    }

    @Transactional(readOnly = true)
    public List<User> findFriends(Integer userId) {
        String sql = "SELECT u.id, u.login, u.name, u.email, u.birthday FROM users u" +
                " JOIN user_friends uf ON u.id = uf.friend_id WHERE uf.user_id = ?";
        return findMany(sql, userId);
    }

    public List<User> findMutualFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT u.id, u.login, u.name, u.email, u.birthday FROM users u JOIN user_friends uf1" +
                " ON u.id = uf1.friend_id AND uf1.user_id = ?" +
                " JOIN user_friends uf2 ON u.id = uf2.friend_id AND uf2.user_id = ? ";
        return findMany(sql, userId, otherUserId);
    }
}
