package com.gymhub.gymhub.domain;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;

import java.sql.Date;

@Entity
@Schema(description = "Details unique to moderators")
@Table(name = "Moderator")
public class Moderator extends ForumAccount {

    // Default constructor
    public Moderator() {
    }

    // Constructor with all fields, correctly assigns values
    public Moderator(Long id, String userName, String password, String email) {
        super(id, userName, password, email);
    }

    // Ensure this constructor sets the fields properly
    public Moderator(String userName, String password, String email, Date date) {
        super(null, userName, password, email);
    }

    // Another constructor example without the 'id'
    public Moderator(String userName, String password, String email) {
        super(null, userName, password, email);
    }
}
