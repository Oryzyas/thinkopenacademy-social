package com.thinkopen.jdbctest;

import org.mariadb.jdbc.MySQLDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLAccess {

    private Connection con = null;
    //private PreparedStatement ps = null;

    private Connection getConnection() throws SQLException {
        /* Metodo 1

        // Caricare il driver
        Class.forName("com.mysql.jdbc.Driver");

        // Setup della Connection
        con = DriverManager.getConnection("jdbc:mysql://localhost/jdbctest?user=root&password=root");
        return con;

        */

        if(con == null) {
            // Metodo 2

            MySQLDataSource dataSource = new MySQLDataSource();
            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306);
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setDatabaseName("jdbctest");
            con = dataSource.getConnection();
        }

        return con;
    }

    public User selectById(int id) throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM users WHERE id=?";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Settare eventuali parametri necessari per eseguire la query
        ps.setInt(1, id);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        User user = null;

        while(rs.next()) {
            user = new User();
            user.setId(id);
            user.setNome(rs.getString("nome"));
            user.setEta(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));
        }

        return user;
    }

    public Comment selectCommentById(int id) throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM comments WHERE id=?";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Settare eventuali parametri necessari per eseguire la query
        ps.setInt(1, id);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        Comment comment = null;

        while(rs.next()) {
            comment = new Comment();
            comment.setId(id);
            comment.setPostId(rs.getInt("postId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setContent(rs.getString("content"));
            comment.setDate(rs.getLong("date"));
        }

        return comment;
    }

    public Post selectPostById(int id) throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM posts WHERE id=?";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Settare eventuali parametri necessari per eseguire la query
        ps.setInt(1, id);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        Post post = null;

        while(rs.next()) {
            post = new Post();
            post.setId(id);
            post.setUserId(rs.getInt("userId"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setClosed(rs.getBoolean("isClosed"));
            post.setDate(rs.getLong("date"));
        }

        return post;
    }

    public int insert(User user, String password) throws SQLException, ClassNotFoundException {
        final String query = "INSERT INTO users(email, password, nome, eta) VALUES(?, md5(?), ?, ?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, password);
        ps.setString(3, user.getNome());
        ps.setInt(4, user.getEta());

        ps.executeUpdate();

        return getLastInsertId();
    }

    public int insert(Post post) throws SQLException, ClassNotFoundException {
        final String query = "INSERT INTO posts(userId, title, content, date, isClosed) VALUES(?, ?, ?, ?, ?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, post.getUserId());
        ps.setString(2, post.getTitle());
        ps.setString(3, post.getContent());
        ps.setLong(4, post.getDate());
        ps.setBoolean(5, post.isClosed());

        ps.executeUpdate();

        return getLastInsertId();
    }

    public int insert(Comment comment) throws SQLException, ClassNotFoundException {
        String query = "insert into comments (postId, userId, content, date) values (?, ?, ?, ?)";
        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, comment.getPostId());
        ps.setInt(2, comment.getUserId());
        ps.setString(3, comment.getContent());
        ps.setLong(4, comment.getDate());
        ps.executeUpdate();

        return getLastInsertId();
    }

    private int getLastInsertId() throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT LAST_INSERT_ID() AS lastId");
        ResultSet rs = ps.executeQuery();
        return (rs.next()) ? rs.getInt("lastId") : -1;
    }

    public int update(User user) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE users SET email=?, nome=?, eta=? WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getNome());
        ps.setInt(3, user.getEta());
        ps.setInt(4, user.getId());

        return ps.executeUpdate();
    }

    public int update(Post post) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE posts SET title=?, content=?, date=?, isClosed=? WHERE id=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);

        ps.setString(1, post.getTitle());
        ps.setString(2, post.getContent());
        ps.setLong(3, post.getDate());
        ps.setBoolean(4, post.isClosed());

        ps.setInt(5, post.getId());
        ps.setInt(6, post.getUserId());

        return ps.executeUpdate();
    }

    public int update(Comment comment) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE comments SET content=?, date=? WHERE id=? AND postId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);

        ps.setString(1, comment.getContent());
        ps.setLong(2, comment.getDate());

        ps.setInt(3, comment.getId());
        ps.setInt(4, comment.getPostId());
        ps.setInt(5, comment.getUserId());

        return ps.executeUpdate();
    }

    public int update(int id, String password) throws SQLException, ClassNotFoundException {
        final String query = "UPDATE users SET password=md5(?) WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, password);
        ps.setInt(2, id);

        return ps.executeUpdate();
    }

    public int delete(int id) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM users WHERE id=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, id);

        return ps.executeUpdate();
    }

    public int delete(Post post) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM posts WHERE id=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, post.getId());
        ps.setInt(2, post.getUserId());

        return ps.executeUpdate();
    }

    public int delete(Comment comment) throws SQLException, ClassNotFoundException {
        final String query = "DELETE FROM comments WHERE id=? AND postId=? AND userId=?";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setInt(1, comment.getId());
        ps.setInt(2, comment.getPostId());
        ps.setInt(3, comment.getUserId());

        return ps.executeUpdate();
    }

    public User login(String email, String password) throws SQLException, ClassNotFoundException {
        final String query = "SELECT * FROM users WHERE email=? AND password=md5(?)";

        PreparedStatement ps = getConnection().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        User user = null;

        while(rs.next()) {
            user = new User();
            user.setId(rs.getInt("id"));
            user.setNome(rs.getString("nome"));
            user.setEta(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));
        }

        return user;
    }

    public List<User> selectAll() throws SQLException, ClassNotFoundException {
        // Scrivere una query
        final String query = "SELECT * FROM users";

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet

        List<User> list = new ArrayList<>();

        while(rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNome(rs.getString("nome"));
            user.setEta(rs.getInt("eta"));
            user.setEmail(rs.getString("email"));

            list.add(user);
        }

        return list;
    }

    public List<Post> selectAllPosts(Integer userId, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM posts");

        if(userId != null)
            queryBuilder.append(" WHERE userId=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("userId=?"))
            ps.setInt(phCounter++, userId);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Post> posts = new ArrayList<>();

        while(rs.next()) {
            Post post = new Post();

            post.setId(rs.getInt("id"));
            post.setUserId(rs.getInt("userId"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setClosed(rs.getBoolean("isClosed"));
            post.setDate(rs.getLong("date"));

            posts.add(post);
        }

        return posts;
    }

    public List<Comment> selectAllComments(Integer postId, Integer limit, Integer offset, int sortedByDate) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM comments");

        if(postId != null)
            queryBuilder.append(" WHERE postId=?");

        if(sortedByDate < 0)
            queryBuilder.append(" ORDER BY date DESC");
        else if(sortedByDate > 0)
            queryBuilder.append(" ORDER BY date ASC");

        if(limit != null)
            queryBuilder.append(" LIMIT ?");

        if(offset != null)
            queryBuilder.append(" OFFSET ?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);
        int phCounter = 1;

        if(query.contains("postId=?"))
            ps.setInt(phCounter++, postId);

        if(query.contains("LIMIT"))
            ps.setInt(phCounter++, limit);

        if(query.contains("OFFSET"))
            ps.setInt(phCounter, offset);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        // Leggere il ResultSet
        List<Comment> comments = new ArrayList<>();

        while(rs.next()) {
            Comment comment = new Comment();

            comment.setId(rs.getInt("id"));
            comment.setPostId(rs.getInt("postId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setContent(rs.getString("content"));
            comment.setDate(rs.getLong("date"));

            comments.add(comment);
        }

        return comments;
    }

    public int countPosts(Integer userId) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) AS N FROM posts");

        if(userId != null)
            queryBuilder.append(" WHERE userId=?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        if(query.contains("userId=?"))
            ps.setInt(1, userId);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        if(rs.next())
            return rs.getInt("N");

        return -1;
    }

    public int countComments(Integer postId) throws SQLException, ClassNotFoundException {
        final StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) AS N FROM comments");

        if(postId != null)
            queryBuilder.append(" WHERE postId=?");

        final String query = queryBuilder.toString();

        // PreparedStatement
        PreparedStatement ps = getConnection().prepareStatement(query);

        if(query.contains("postId=?"))
            ps.setInt(1, postId);

        // Eseguire la query (output: ResultSet)
        ResultSet rs = ps.executeQuery();

        if(rs.next())
            return rs.getInt("N");

        return -1;
    }
}
