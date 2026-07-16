/*
 * C program to implement Stop and Wait protocol
 * This is a simplified simulation and does not involve actual network sockets.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <unistd.h>

#define MAX_DATA 10
#define TIMEOUT_SECONDS 3

// Simulate sending a frame
void send_frame(int frame_num, char data) {
    printf("Sender: Sending frame %d with data '%c'\n", frame_num, data);
}

// Simulate receiving a frame
bool receive_frame(int expected_frame_num, char *received_data) {
    // Simulate network delay and potential loss
    sleep(rand() % 3 + 1); // Simulate delay between 1 and 3 seconds

    if (rand() % 5 == 0) { // Simulate 20% frame loss
        printf("Receiver: Frame %d lost!\n", expected_frame_num);
        return false;
    }

    *received_data = 'A' + (expected_frame_num % 26); // Simulate some data
    printf("Receiver: Received frame %d with data '%c'\n", expected_frame_num, *received_data);
    return true;
}

// Simulate sending an acknowledgment
void send_ack(int ack_num) {
    printf("Receiver: Sending ACK %d\n", ack_num);
}

// Simulate receiving an acknowledgment
bool receive_ack(int expected_ack_num) {
    // Simulate network delay and potential loss
    sleep(rand() % 2 + 1); // Simulate delay between 1 and 2 seconds

    if (rand() % 4 == 0) { // Simulate 25% ACK loss
        printf("Sender: ACK %d lost!\n", expected_ack_num);
        return false;
    }

    printf("Sender: Received ACK %d\n", expected_ack_num);
    return true;
}

int main() {
    srand(time(NULL));

    int sender_frame_num = 0;
    int receiver_expected_frame_num = 0;
    char data_to_send = 'A';

    printf("--- Stop and Wait Protocol Simulation ---\n");

    while (sender_frame_num < MAX_DATA) {
        // Sender side
        send_frame(sender_frame_num, data_to_send);

        time_t start_time = time(NULL);
        bool ack_received = false;

        while (!ack_received && (time(NULL) - start_time < TIMEOUT_SECONDS)) {
            // In a real scenario, sender would wait for an event (ACK or timeout)
            // For simulation, we'll just check periodically
            if (receive_ack(sender_frame_num)) {
                ack_received = true;
                break;
            }
            // Small sleep to prevent busy-waiting in simulation
            usleep(500000); // 0.5 seconds
        }

        if (ack_received) {
            printf("Sender: Frame %d acknowledged. Moving to next frame.\n\n", sender_frame_num);
            sender_frame_num++;
            data_to_send++;
        } else {
            printf("Sender: Timeout for frame %d. Resending frame.\n\n", sender_frame_num);
            // No change in sender_frame_num, resend the same frame
        }

        // Receiver side (simplified, running in the same loop for simulation)
        char received_data;
        if (receive_frame(receiver_expected_frame_num, &received_data)) {
            send_ack(receiver_expected_frame_num);
            receiver_expected_frame_num++;
        } else {
            // If frame lost, receiver does nothing (implicitly waits for retransmission)
            // Or, if it received a duplicate, it would resend the last ACK
            // For simplicity, we assume it just waits for the correct frame
        }
    }

    printf("--- Simulation Complete ---\n");

    return 0;
}
