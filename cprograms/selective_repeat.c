/*
 * C program to implement a simplified Selective Repeat Protocol
 * This is a simulation and does not involve actual network sockets.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <unistd.h>

#define WINDOW_SIZE 4
#define MAX_SEQ_NUM (2 * WINDOW_SIZE) // Sequence numbers 0 to 7 for window size 4
#define MAX_DATA 20
#define TIMEOUT_SECONDS 3

// Frame structure
typedef struct {
    int seq_num;
    char data;
    bool is_sent;
    bool is_acked;
    time_t timer;
} Frame;

// Receiver buffer
typedef struct {
    int seq_num;
    char data;
    bool is_received;
} ReceiverBufferSlot;

// Simulate sending a frame
void send_frame(int seq_num, char data) {
    printf("Sender: Sending frame %d with data \'%c\'\n", seq_num, data);
}

// Simulate receiving a frame
bool receive_frame(int expected_seq_num, char *received_data, int *actual_seq_num) {
    // Simulate network delay and potential loss
    sleep(rand() % 2 + 1); // Simulate delay between 1 and 2 seconds

    *actual_seq_num = rand() % MAX_SEQ_NUM; // Simulate receiving any sequence number

    if (rand() % 6 == 0) { // Simulate ~16% frame loss
        printf("Receiver: Frame %d (expected %d) lost!\n", *actual_seq_num, expected_seq_num);
        return false;
    }

    *received_data = 'A' + (*actual_seq_num % 26); // Simulate some data
    printf("Receiver: Received frame %d with data \'%c\'\n", *actual_seq_num, *received_data);
    return true;
}

// Simulate sending an acknowledgment
void send_ack(int ack_num) {
    printf("Receiver: Sending ACK %d\n", ack_num);
}

// Simulate receiving an acknowledgment
bool receive_ack(int *acked_seq_num) {
    // Simulate network delay and potential loss
    sleep(rand() % 1 + 1); // Simulate delay of 1 second

    if (rand() % 5 == 0) { // Simulate 20% ACK loss
        printf("Sender: ACK lost!\n");
        return false;
    }

    *acked_seq_num = rand() % MAX_SEQ_NUM; // Simulate receiving any ACK
    printf("Sender: Received ACK %d\n", *acked_seq_num);
    return true;
}

int main() {
    srand(time(NULL));

    Frame sender_window[MAX_SEQ_NUM];
    ReceiverBufferSlot receiver_buffer[MAX_SEQ_NUM];

    int sender_base = 0;
    int sender_next_seq_num = 0;
    int receiver_base = 0;

    // Initialize sender window and receiver buffer
    for (int i = 0; i < MAX_SEQ_NUM; i++) {
        sender_window[i].is_sent = false;
        sender_window[i].is_acked = false;
        receiver_buffer[i].is_received = false;
    }

    printf("-- Selective Repeat Protocol Simulation --\n");

    int data_to_send_count = 0;
    int data_received_count = 0;

    while (data_received_count < MAX_DATA) {
        // Sender side: Send new frames if window allows
        while (sender_next_seq_num < sender_base + WINDOW_SIZE && data_to_send_count < MAX_DATA) {
            int current_seq = sender_next_seq_num % MAX_SEQ_NUM;
            sender_window[current_seq].seq_num = sender_next_seq_num;
            sender_window[current_seq].data = 'A' + (data_to_send_count % 26);
            sender_window[current_seq].is_sent = true;
            sender_window[current_seq].is_acked = false;
            sender_window[current_seq].timer = time(NULL);
            send_frame(sender_window[current_seq].seq_num, sender_window[current_seq].data);
            sender_next_seq_num++;
            data_to_send_count++;
        }

        // Sender side: Check for ACKs and timeouts
        int acked_seq;
        if (receive_ack(&acked_seq)) {
            if (acked_seq >= sender_base && acked_seq < sender_next_seq_num) {
                sender_window[acked_seq % MAX_SEQ_NUM].is_acked = true;
                printf("Sender: Frame %d acknowledged.\n", acked_seq);
            }
        }

        // Advance sender_base if frames are acknowledged sequentially
        while (sender_window[sender_base % MAX_SEQ_NUM].is_acked && sender_base < sender_next_seq_num) {
            printf("Sender: Advancing window, base is now %d\n", sender_base + 1);
            sender_window[sender_base % MAX_SEQ_NUM].is_sent = false;
            sender_window[sender_base % MAX_SEQ_NUM].is_acked = false;
            sender_base++;
        }

        // Check for timeouts and retransmit individual frames
        for (int i = sender_base; i < sender_next_seq_num; i++) {
            int current_seq = i % MAX_SEQ_NUM;
            if (sender_window[current_seq].is_sent && !sender_window[current_seq].is_acked && (time(NULL) - sender_window[current_seq].timer > TIMEOUT_SECONDS)) {
                printf("Sender: Timeout for frame %d. Retransmitting.\n", sender_window[current_seq].seq_num);
                send_frame(sender_window[current_seq].seq_num, sender_window[current_seq].data);
                sender_window[current_seq].timer = time(NULL); // Restart timer
            }
        }

        // Receiver side: Receive frames and send ACKs
        char rcv_data;
        int rcv_seq_num;
        if (receive_frame(receiver_base, &rcv_data, &rcv_seq_num)) {
            if (rcv_seq_num >= receiver_base && rcv_seq_num < receiver_base + WINDOW_SIZE) {
                // Frame is within receiver's window
                if (!receiver_buffer[rcv_seq_num % MAX_SEQ_NUM].is_received) {
                    receiver_buffer[rcv_seq_num % MAX_SEQ_NUM].seq_num = rcv_seq_num;
                    receiver_buffer[rcv_seq_num % MAX_SEQ_NUM].data = rcv_data;
                    receiver_buffer[rcv_seq_num % MAX_SEQ_NUM].is_received = true;
                    printf("Receiver: Stored frame %d in buffer.\n", rcv_seq_num);
                }
                send_ack(rcv_seq_num); // Always ACK the received frame
            } else if (rcv_seq_num < receiver_base) {
                // Duplicate frame, resend ACK
                printf("Receiver: Duplicate frame %d received, resending ACK.\n", rcv_seq_num);
                send_ack(rcv_seq_num);
            }
        }

        // Deliver data to upper layer and advance receiver_base
        while (receiver_buffer[receiver_base % MAX_SEQ_NUM].is_received) {
            printf("Receiver: Delivering data \'%c\' from frame %d to upper layer.\n", receiver_buffer[receiver_base % MAX_SEQ_NUM].data, receiver_buffer[receiver_base % MAX_SEQ_NUM].seq_num);
            receiver_buffer[receiver_base % MAX_SEQ_NUM].is_received = false;
            receiver_base++;
            data_received_count++;
        }

        // Small sleep to prevent busy-waiting in simulation
        usleep(500000); // 0.5 seconds
    }

    printf("-- Simulation Complete --\n");

    return 0;
}
