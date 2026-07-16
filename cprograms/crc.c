/*
 * C program to implement Cyclic Redundancy Check (CRC) error detection protocol.
 * This is a simplified implementation for demonstration purposes.
 */

#include <stdio.h>
#include <string.h>

// Function to perform XOR operation
void xor_op(char *dividend, const char *divisor) {
    for (int i = 0; i < strlen(divisor); i++) {
        if (dividend[i] == divisor[i]) {
            dividend[i] = '0';
        } else {
            dividend[i] = '1';
        }
    }
}

// Function to perform binary division (CRC calculation)
void crc_calculate(char *data, const char *key, char *remainder) {
    int data_len = strlen(data);
    int key_len = strlen(key);

    // Append (key_len - 1) zeros to the data
    char temp_data[100];
    strcpy(temp_data, data);
    for (int i = 0; i < key_len - 1; i++) {
        strcat(temp_data, "0");
    }

    char current_dividend[100];
    strncpy(current_dividend, temp_data, key_len);
    current_dividend[key_len] = '\0';

    for (int i = 0; i < data_len; i++) {
        if (current_dividend[0] == '1') {
            xor_op(current_dividend, key);
        }

        // Shift and bring next bit
        for (int j = 0; j < key_len - 1; j++) {
            current_dividend[j] = current_dividend[j+1];
        }
        current_dividend[key_len - 1] = temp_data[i + key_len];
        current_dividend[key_len] = '\0';
    }

    // The last (key_len - 1) bits are the remainder
    strncpy(remainder, current_dividend, key_len - 1);
    remainder[key_len - 1] = '\0';
}

int main() {
    char data[100];
    char key[100];
    char remainder[100];
    char transmitted_data[100];

    printf("--- CRC Error Detection Simulation ---\n");

    printf("Enter data in binary (e.g., 1010001101): ");
    scanf("%s", data);

    printf("Enter generator polynomial (key) in binary (e.g., 110101): ");
    scanf("%s", key);

    // Sender side
    crc_calculate(data, key, remainder);
    printf("\nSender: Remainder (CRC) = %s\n", remainder);

    strcpy(transmitted_data, data);
    strcat(transmitted_data, remainder);
    printf("Sender: Transmitted data = %s\n", transmitted_data);

    // Receiver side (without error)
    char received_remainder[100];
    crc_calculate(transmitted_data, key, received_remainder);
    printf("\nReceiver (no error): Calculated remainder = %s\n", received_remainder);

    if (strcmp(received_remainder, "00000") == 0) { // Assuming key_len-1 zeros
        printf("Receiver (no error): No error detected.\n");
    } else {
        printf("Receiver (no error): Error detected!\n");
    }

    // Receiver side (with simulated error)
    printf("\nSimulating error in transmitted data...\n");
    // Flip a bit for error simulation
    if (strlen(transmitted_data) > 2) {
        transmitted_data[strlen(transmitted_data) - 2] = (transmitted_data[strlen(transmitted_data) - 2] == '0') ? '1' : '0';
    }
    printf("Receiver (with error): Received data = %s\n", transmitted_data);

    crc_calculate(transmitted_data, key, received_remainder);
    printf("Receiver (with error): Calculated remainder = %s\n", received_remainder);

    if (strcmp(received_remainder, "00000") == 0) { // Assuming key_len-1 zeros
        printf("Receiver (with error): No error detected (false negative - very unlikely with CRC)!\n");
    } else {
        printf("Receiver (with error): Error detected!\n");
    }

    printf("--- Simulation Complete ---\n");

    return 0;
}
