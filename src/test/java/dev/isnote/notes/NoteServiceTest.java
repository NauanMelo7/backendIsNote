package dev.isnote.notes;

import dev.isnote.notesDocument.NoteRepository;
import dev.isnote.notesDocument.NoteService;
import dev.isnote.user.RoleUser;
import dev.isnote.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User("Nauan", "nauan@isnote.dev", "encoded-password", RoleUser.USER);
        user.setId(UUID.randomUUID());
    }
}
