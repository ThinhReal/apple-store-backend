package com.thinhreal.applestore.model.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserEntityTest {

    private static final String VALID_FIRST_NAME = "John";
    private static final String VALID_LAST_NAME = "Doe";
    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final String VALID_PASSWORD = "Secure1!";
    private static final String VALID_ADDRESS = "123 Main Street";

    @Test
    void constructor_whenAllFieldsValid_createsUserEntity() {
        // Given
        // valid field constants defined above

        // When
        UserEntity user = new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_ADDRESS
        );

        // Then
        assertThat(user.getFirst_name()).isEqualTo(VALID_FIRST_NAME);
        assertThat(user.getLast_name()).isEqualTo(VALID_LAST_NAME);
        assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
        assertThat(user.getAddress()).isEqualTo(VALID_ADDRESS);
    }

    @Test
    void constructor_whenFirstNameIsNull_throwsIllegalArgumentException() {
        // Given
        String firstName = null;

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                firstName,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your First or Last Name is empty, please check again");
    }

    @Test
    void constructor_whenFirstNameIsBlank_throwsIllegalArgumentException() {
        // Given
        String firstName = "   ";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                firstName,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your First or Last Name is empty, please check again");
    }

    @Test
    void constructor_whenLastNameIsNull_throwsIllegalArgumentException() {
        // Given
        String lastName = null;

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                lastName,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your First or Last Name is empty, please check again");
    }

    @Test
    void constructor_whenLastNameIsBlank_throwsIllegalArgumentException() {
        // Given
        String lastName = "";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                lastName,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your First or Last Name is empty, please check again");
    }

    @Test
    void constructor_whenEmailIsNull_throwsIllegalArgumentException() {
        // Given
        String email = null;

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                email,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing Email Field");
    }

    @Test
    void constructor_whenEmailIsBlank_throwsIllegalArgumentException() {
        // Given
        String email = "  ";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                email,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing Email Field");
    }

    @Test
    void constructor_whenEmailIsInvalid_throwsIllegalArgumentException() {
        // Given
        String email = "not-an-email";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                email,
                VALID_PASSWORD,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Email");
    }

    @Test
    void constructor_whenPasswordIsTooShort_throwsIllegalArgumentException() {
        // Given
        String password = "Sec1!";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                password,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password does not meet the criteria");
    }

    @Test
    void constructor_whenPasswordHasNoUppercase_throwsIllegalArgumentException() {
        // Given
        String password = "secure1!";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                password,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password does not meet the criteria");
    }

    @Test
    void constructor_whenPasswordHasNoSpecialCharacter_throwsIllegalArgumentException() {
        // Given
        String password = "Secure12";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                password,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password does not meet the criteria");
    }

    @Test
    void constructor_whenPasswordContainsWhitespace_throwsIllegalArgumentException() {
        // Given
        String password = "Secure 1!";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                password,
                VALID_ADDRESS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password does not meet the criteria");
    }

    @Test
    void constructor_whenAddressIsNull_throwsIllegalArgumentException() {
        // Given
        String address = null;

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                address
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your address is empty, please check again");
    }

    @Test
    void constructor_whenAddressIsBlank_throwsIllegalArgumentException() {
        // Given
        String address = "   ";

        // When / Then
        assertThatThrownBy(() -> new UserEntity(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                address
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Your address is empty, please check again");
    }
}
