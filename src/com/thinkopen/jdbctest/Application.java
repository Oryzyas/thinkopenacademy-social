package com.thinkopen.jdbctest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Application {
    private static final MySQLAccess dao;
    private static final Scanner scanner;
    private static User user;

    static {
        dao = new MySQLAccess();
        scanner = new Scanner(System.in);
    }
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
        System.out.println("Benvenuto. Inserire username e password.");

        String email = read("Email", t -> t.length() > 0 && t.length() <= 30, "Email non valida.");
        String password = read("Password", t -> t.length() > 0 && t.length() <= 20, "Password non valida.");

        if((user = dao.login(email, password)) != null) {
            System.out.println("Utente loggato con successo.");

            while(true) {
                mainMenu();
            }
        } else {
            System.out.println("Credenziali non valide.");

            if(read("Creare un nuovo utente?", str -> Boolean.parseBoolean(str), risp -> true, "Risposta non valida.")) {
                createUser();
            }
        }
	}

	private static void mainMenu() throws SQLException, ClassNotFoundException {
        final String msg = "1 - Visualizza gli ultimi post ;\n2 - Gestisci i propri post ;\n3 - Cambia password ;\n4 - Esci ;\n\nRisposta";

        int risp = read(msg, s -> Integer.parseInt(s), r -> r >= 1 && r <= 4, "Risposta non valida.");

        switch (risp) {
            case 1:
                showLastPosts();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    private static void showLastPosts() throws SQLException, ClassNotFoundException {
        List<Post> posts;
        int limit = 3;
        int offset = 0;

        boolean morePostsRequest = false;

        while((posts = dao.selectAllPostsByLimit(limit, offset)).size() > 0) {
            offset += limit;
            morePostsRequest = false;

            for(Post p : posts) {
                System.out.println(p);
            }

            System.out.println();
            int risp = postMenu();

            switch (risp) {
                case 1:
                    commentMenu();
                    break;
                case 2:
                    morePostsRequest = true;
                    continue;
                case 3:
                    return;
            }
        }

        if(morePostsRequest)
            showError("Non ci sono ulteriori post da visualizzare.");

        System.out.println();
    }

    private static int postMenu() {
        final String msg = "1 - Visualizza i commenti di un post;\n2 - Visualizza altri post;\n3 - Torna indietro;\n\nRisposta: ";

        return read(msg, r -> Integer.parseInt(r), r -> r >= 1 && r <= 3, "Risposta non valida.");
    }

    private static void commentMenu() {
        final String msg = "1 - Aggiungi un commento;\n2 - Modifica un commento;\n3 - Elimina un commento;\n4 - Torna indietro\n\nRisposta: ";

        int risp = read(msg, r -> Integer.parseInt(r), r -> r >= 1 && r <= 4, "Risposta non valida.");

        switch (risp) {
            case 1:
                try {
                    createComment();
                    showSuccess("Creazione commento eseguita con successo.");
                } catch (Exception e) {
                    //e.printStackTrace();
                    showError("Creazione commento fallita.");
                }
                break;
            case 2:
                try {
                    createComment();
                    showSuccess("Creazione commento eseguita con successo.");
                } catch (Exception e) {
                    //e.printStackTrace();
                    showError("Creazione commento fallita.");
                }
                break;
            case 3:
                break;
        }
    }

	private static <T> T read(String msg, Function<String, T> f, Predicate<T> test, String errMsg) {
        while(true) {
            try {
                System.out.print(msg + ": ");
                String input = scanner.nextLine();
                T t = f.apply(input);

                if(!test.test(t))
                    throw new Exception();

                return t;
            } catch (Exception e) {
                showError(errMsg);
            } finally {
                System.out.println();
            }

        }
    }

    private static void showSuccess(String msg) {
        System.out.println("\n\t[ SUCCESS ]: " + msg);
    }

    private static void showError(String errMsg) {
        System.out.println("\n\t[ ERRORE ]: " + errMsg);
    }

    private static String read(String msg, Predicate<String> test, String errMsg) {
        return read(msg, str -> str, test, errMsg);
    }

    private static User createUser() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setNome(read("Nome", n -> n.length() > 0 && n.length() <= 50, "Lunghezza nome non valida."));
        user.setEmail(read("Email", e -> e.length() > 0 && e.length() <= 30, "Lunghezza mail non valida."));
        String password = read("Password", p -> p.length() > 0 && p.length() <= 20, "Lunghezza password non valida");
        user.setEta(read("Età", str -> Integer.parseInt(str), e -> e > 0 && e <= 100, "Eta non valida."));

        dao.insert(user, password);

        return user;
    }

    private static Post createPost() throws SQLException, ClassNotFoundException {
        Post post = new Post();
        post.setTitle(read("Titolo", t -> t.length() > 0 && t.length() <= 255, "Lunghezza titolo non valida."));
        post.setContent(read("Contenuto", t -> t.length() > 0 && t.length() <= 1024, "Lunghezza contenuto non valida."));
        post.setClosed(read("Chiuso ai commenti?", str -> Boolean.parseBoolean(str), t -> true, "Risposta non valida."));
        post.setUserId(user.getId());

        dao.insert(post);

        return post;
    }
	
	private static Comment createComment() throws ClassNotFoundException, SQLException {
        int post = read("Post ID", str -> Integer.parseInt(str), postId -> postExists(postId), "ID post non valido.");

        if(!postIsOpen(post)) {
            showError("Il post è chiuso ai commenti.");
            return null;
        }

        String content = read("Commento", c -> c.length() > 0 && c.length() <= 140, "Lunghezza commento non valida.");
		
		Comment dto = new Comment();
		dto.setContent(content);
		dto.setPostId(post);
		dto.setUserId(user.getId());
		dto.setDate(System.currentTimeMillis());
		
		dao.insert(dto);
		
		return dto;
	}

    /*private static Comment editComment() throws ClassNotFoundException, SQLException {
        int commentID = read("Comment ID", str -> Integer.parseInt(str), postId -> postExists(postId), "ID post non valido.");

        if(!postIsOpen(post)) {
            showError("Il post è chiuso ai commenti.");
            return null;
        }

        String content = read("Commento", c -> c.length() > 0 && c.length() <= 140, "Lunghezza commento non valida.");

        Comment dto = new Comment();
        dto.setContent(content);
        dto.setPostId(post);
        dto.setUserId(user.getId());
        dto.setDate(System.currentTimeMillis());

        dao.insert(dto);

        return dto;
    }*/

	private static boolean userExists(int id) {
        try {
            return dao.selectById(id) != null;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean postExists(int id) {
        try {
            return dao.selectPostById(id) != null;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean postIsOpen(int id) {
        try {
            Post post = dao.selectPostById(id);

            if(post == null)
                return false;

            return !post.isClosed();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean commentExists(int id) {
        try {
            return dao.selectCommentById(id) != null;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
