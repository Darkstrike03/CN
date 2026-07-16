/*
 * C program to implement a simplified Sliding Window Protocol (Go-Back-N variant)
 * This is a simulation and does not involve actual network sockets.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <unistd.h>

#define WINDOW_SIZE 4
#define MAX_SEQ_NUM 8 // Sequence numbers 0 to 7
#define MAX_DATA 20
#define TIMEOUT_SECONDS 3

// Simulate sending a frame
void send_frame(int frame_num, char data) {
    printf("Sender: Sending frame %d with data \'%c\'\n", frame_num, data);
}

// Simulate receiving a frame
bool receive_frame(int expected_frame_num, char *received_data) {
    // Simulate network delay and potential loss
    sleep(rand() % 2 + 1); // Simulate delay between 1 and 2 seconds

    if (rand() % 7 == 0) { // Simulate ~14% frame loss
        printf("Receiver: Frame %d lost!\n", expected_frame_num);
        return false;
    }

    *received_data = 'A' + (expected_frame_num % 26); // Simulate some data
    printf("Receiver: Received frame %d with data \'%c\'\n", expected_frame_num, *received_data);
    return true;
}

// Simulate sending an acknowledgment
void send_ack(int ack_num) {
    printf("Receiver: Sending ACK %d\n", ack_num);
}

// Simulate receiving an acknowledgment
bool receive_ack(int expected_ack_num) {
    // Simulate network delay and potential loss
    sleep(rand() % 1 + 1); // Simulate delay of 1 second

    if (rand() % 6 == 0) { // Simulate ~16% ACK loss
        printf("Sender: ACK %d lost!\n", expected_ack_num);
        return false;
    }

    printf("Sender: Received ACK %d\n", expected_ack_num);
    return true;
}

int main() {
    srand(time(NULL));

    int base = 0; // Oldest unacknowledged frame
    int next_seq_num = 0; // Next frame to be sent
    char data_buffer[MAX_SEQ_NUM]; // Buffer to store data for retransmission
    time_t timer[MAX_SEQ_NUM]; // Timers for each frame in the window

    printf("--- Sliding Window Protocol (Go-Back-N) Simulation ---\n");

    int frames_sent_count = 0;
    while (frames_sent_count < MAX_DATA || base != next_seq_num) {
        // Sender side: Send frames within the window
        while (next_seq_num < base + WINDOW_SIZE && frames_sent_count < MAX_DATA) {
            data_buffer[next_seq_num % MAX_SEQ_NUM] = 'A' + (frames_sent_count % 26);
            send_frame(next_seq_num, data_buffer[next_seq_num % MAX_SEQ_NUM]);
            timer[next_seq_num % MAX_SEQ_NUM] = time(NULL);
            next_seq_num = (next_seq_num + 1) % MAX_SEQ_NUM;
            frames_sent_count++;
        }

        // Simulate receiver processing (simplified, running in the same loop)
        char received_data;
        if (receive_frame(base, &received_data)) {
            send_ack(base);
            base = (base + 1) % MAX_SEQ_NUM;
        }

        // Sender side: Check for ACKs and timeouts
        if (base != next_seq_num) { // If there are unacknowledged frames
            // Check for ACKs
            if (receive_ack(base)) {
                // ACK for base received, advance window
                // In Go-Back-N, cumulative ACK means all frames up to base are acknowledged
                // For simplicity, we just advance base if the ACK for base is received
                // A more robust simulation would check for ACKs for any frame in the window
            } else if (time(NULL) - timer[base % MAX_SEQ_NUM] > TIMEOUT_SECONDS) {
                printf("Sender: Timeout for frame %d. Resending all frames from base %d.\n", base, base);
                // Retransmit all frames from 'base' to 'next_seq_num - 1'
                for (int i = base; i < next_seq_num; i++) {
                    send_frame(i, data_buffer[i % MAX_SEQ_NUM]);
                    timer[i % MAX_SEQ_NUM] = time(NULL); // Restart timer for retransmitted frames
                }
            }
        }

        // Small sleep to prevent busy-waiting in simulation
        usleep(500000); // 0.5 seconds
    }

    printf("--- Simulation Complete ---\n");

    return 0;
}
