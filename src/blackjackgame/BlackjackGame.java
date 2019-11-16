package blackjackgame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BlackjackGame {

    static Random rand = new Random();
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        boolean again = true;
        long money = 100;
        System.out.println("You have: $" + (long) money);

        while (again == true) {
            long bet = getBet(money);

            long amountWonOrLost = playBLACKJACK(bet);

            money += amountWonOrLost;

            if (money == 0) {
                System.out.println("You ran out of money!");
                break;
            } else {
                System.out.println("You now have: $" + money);
            }

            again = getAgain();
        }

        System.out.println("Your final amount is: " + money);
        System.out.println("GAME OVER");
    }

    public static long getBet(long money) {
        long bet;
        while (true) {
            try {
                System.out.print("How much do you wish to bet? --> ");
                bet = input.nextLong();
                System.out.println("");
                if (bet > money) {
                    throw new Exception("You can't bet more than you have! Please re-enter.");
                }
                return bet;
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                } else {
                    System.out.println("Enter a valid input.");
                    input.next();
                }
            }
        }
    }

    public static String[] deckGenerator() {
        String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

        String[] deck = new String[52];
        for (int i = 0; i < ranks.length; i++) {
            for (int j = 0; j < suits.length; j++) {
                deck[(suits.length * i) + j] = ranks[i] + " of " + suits[j];
            }
        }

        return deck;

    }

    public static int[] getHand(String[] deck, String player) {

        int[] cardAndValue = new int[2];

        boolean valid = false;
        int[] usedCards = new int[deck.length];
        int hand = 0;

        while (valid == false) {
            int randomCardIndex = rand.nextInt(deck.length);
            if (isInArray(usedCards, randomCardIndex) == false) { // if the card is not in the usedCards array
                hand += valueOfCard(deck, randomCardIndex, player); // make hand the value of that card
                valid = true; // make valid true to end while
            } else {
                valid = false; // else if the card is in the array, valid is false and another card is chosen
            }

            cardAndValue[0] = randomCardIndex;
            cardAndValue[1] = hand;
        }
        return cardAndValue;
    }

    public static boolean isInArray(int[] array, int position) {
        boolean result = false;

        for (int i = 0; i < array.length; i++) {
            if (array[i] == position) {
                result = true;
                break;
            }
        }
        return result;

    }

    public static int valueOfCard(String[] deck, int index, String player) {
        int value = 0;
        char currentCard = deck[index].charAt(0);
        if (currentCard == '1') { // could be 1 or 10
            if (deck[index].charAt(1) == ' ') { // e.g 1 of spades
                value = 1;
            } else if (deck[index].charAt(1) == '0') { // e.g 10 of spades
                value = 10;
            }
        } else if (Character.isDigit(currentCard) == true) { // any other numbered card
            value = Character.getNumericValue(currentCard);
        } else if (currentCard == 'A') { // ace takes either 11 or 1, whichever the user wishes
            int choice;
            if (player.equals("user")) {
                while (true) {
                    try {
                        System.out.println("Your next card is an ace, do you wish to take it as a 1 or 11? (enter 1 or 11) --> ");
                        choice = input.nextInt();
                        if (choice != 1 && choice != 11) {
                            throw new Exception("Enter either 1 or 11.");
                        }
                        value = choice;
                        break;
                    } catch (Exception e) {
                        if (e.getMessage() != null) {
                            System.out.println(e.getMessage());
                        } else {
                            System.out.println("Enter a valid input.");
                        }
                    }
                }
            }
        } else if (currentCard == 'K' || currentCard == 'Q' || currentCard == 'J') { // face cards all worth 10
            value = 10;
        }

        return value;

    }

    public static long playBLACKJACK(long bet) throws Exception {
        int round = 0;
        int[] cardAndValue;

        String[] deck = deckGenerator(); // to make sure the same deck is used throughout the game
        ArrayList<Integer> dealerCards = new ArrayList<>();

        int userHand = 0;
        int dealerHand = 0;

        cardAndValue = getHand(deck, "user");
        userHand += cardAndValue[1];
        System.out.println("First card: " + deck[cardAndValue[0]]); // tell user their card

        cardAndValue = getHand(deck, "user");
        userHand += cardAndValue[1];
        System.out.println("Second card: " + deck[cardAndValue[0]]);// tell user their card

        cardAndValue = getHand(deck, "dealer");
        dealerHand += cardAndValue[1];
        dealerCards.add(cardAndValue[0]); // so that the dealer's cards can later be revealed

        cardAndValue = getHand(deck, "dealer");
        dealerHand += cardAndValue[1];
        dealerCards.add(cardAndValue[0]); // so that the dealer's cards can later be revealed

        while (true) {
            round += 1;

            String choice = null;

            try {

                if (userHand == 21) {
                    String reason = "blackjack";
                    return endGame("win", reason, bet, deck, dealerCards);
                }

                if (round == 1) {
                    System.out.println("Your total is: " + userHand);
                    System.out.print("\nDo you wish to: \nHIT - get another card. \nSTAND - be satisfied with your current hand. \nDOUBLE DOWN - double your bet, hit once and then stand. \n\nEnter either H, S or DD. --> ");
               } else if (round > 1) {
                    System.out.print("\nDo you wish to: \nHIT - get another card. \nSTAND - be satisfied with your current hand. \n\nEnter either H or S. --> ");
                }

                choice = input.next().toUpperCase();
                System.out.println("");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
            if (choice.equals("H")) { // if user hits
                cardAndValue = getHand(deck, "user");
                userHand += cardAndValue[1];
                System.out.println("Next card: " + deck[cardAndValue[0]]);
                System.out.println("Your total is: " + userHand);

                cardAndValue = getHand(deck, "dealer");
                dealerHand += cardAndValue[1];
                dealerCards.add(cardAndValue[0]); // so that the dealer's cards can later be revealed

                if (userHand > 21) {
                    String reason = "Your hand went over 21!";
                    return endGame("lose", reason, bet, deck, dealerCards);
                } else if (dealerHand > 21) {
                    String reason = "The dealer's hand went over 21!";
                    return endGame("win", reason, bet, deck, dealerCards);
                }
                // repeat at while loop

            } else if (choice.equals("S")) { // if user stands
                System.out.println("You final total is: " + userHand);
                System.out.println("The dealer's total is: " + dealerHand + "\n");
                if (userHand > dealerHand) {
                    String reason = "You beat the dealer's total!";
                    return endGame("win", reason, bet, deck, dealerCards);
                } else if (userHand <= dealerHand) {
                    String reason = "The dealer beat your total!";
                    return endGame("lose", reason, bet, deck, dealerCards);
                }
                break;
            } else if (choice.equals("DD") && round == 1) {
                bet *= 2;
                System.out.println("Your amount to bet is now " + bet);
                cardAndValue = getHand(deck, "user");
                userHand += cardAndValue[1];
                System.out.println("Next card: " + deck[cardAndValue[0]]);
                System.out.println("You final total is: " + userHand);


                cardAndValue = getHand(deck, "dealer");
                dealerHand += cardAndValue[1];
                dealerCards.add(cardAndValue[0]); // so that the dealer's cards can later be revealed
                
                
                if (userHand > 21) {
                    String reason = "Your hand went over 21!";
                    return endGame("lose", reason, bet, deck, dealerCards);
                }
                
                System.out.println("The dealer's total is: " + dealerHand + "\n");
                
                if (userHand > dealerHand) {
                    String reason = "You beat the dealer's total!";
                    return endGame("win", reason, bet, deck, dealerCards);
                } else if (userHand <= dealerHand) {
                    String reason = "The dealer beat your total!";
                    return endGame("lose", reason, bet, deck, dealerCards);
                }
                break;
            } else {
                try {
                    if (round == 1) {
                        throw new Exception("Enter either H, S or DD.");
                    } else {
                        throw new Exception("Enter either H or S.");
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        System.out.println(e.getMessage());
                        round -= 1;
                    }
                }
                
            }
        }
        return -1;
    }

    public static long endGame(String outcome, String reason, long bet, String[] deck, ArrayList<Integer> dealerCards) {
        long amountWonOrLost;
        if (outcome.equals("lose")) {
            System.out.println(reason);
            System.out.println("You lose!\n");
            System.out.println("The dealer's cards were: ");
            for (int i = 0; i < dealerCards.size(); i++) {
                if (i == dealerCards.size() - 1) {
                    System.out.println(deck[dealerCards.get(i)] + "\n");
                } else {
                    System.out.print(deck[dealerCards.get(i)] + ", ");
                }

            }
            amountWonOrLost = (bet * (-1));
            System.out.println("Amount lost: " + (amountWonOrLost * (-1)));
            return amountWonOrLost;

        } else if (outcome.equals("win")) {
            System.out.println(reason);
            System.out.println("You win!\n");
            System.out.println("The dealer's cards were: ");
            for (int i = 0; i < dealerCards.size(); i++) {
                if (i == dealerCards.size() - 1) {
                    System.out.println(deck[dealerCards.get(i)] + "\n");
                } else {
                    System.out.print(deck[dealerCards.get(i)] + ", ");
                }
            }
            if (reason.equals("blackjack")) {
                System.out.println("You ");
                amountWonOrLost = (long) (bet * 1.5);
                System.out.println("Amount won: " + amountWonOrLost);
                return amountWonOrLost;
            } else {
                amountWonOrLost = (bet);
                System.out.println("Amount won: " + amountWonOrLost);
                return amountWonOrLost;
            }
        } else {
            return -1;
        }
    }

    public static boolean getAgain() throws Exception {
        while (true) {
            try {
                System.out.print("Do you wish to play again? (Y/N) --> ");
                String choice = input.next().toUpperCase();
                System.out.println("");

                if (choice.equals("Y")) {
                    return true;
                } else if (choice.equals("N")) {
                    return false;
                } else {
                    throw new Exception("Enter either Y or N.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
