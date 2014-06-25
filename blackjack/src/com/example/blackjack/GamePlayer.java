package com.example.blackjack;
import java.io.*;
import java.util.*;

class GamePlayer {
	ArrayList<Card> cards;
	String playerName;
	public GamePlayer(String name) {
		cards = new ArrayList<Card>();
		playerName = name;
	}
	public String name() {
		return playerName;
	}
	void showCards() {
		for (Card c : cards)
			c.print();
	}
	public Card card(int i) {
		if (i < 0 || i >= cards.size())
			return null;
		return cards.get(i);
	}
	public int cardCount() {
		return cards.size();
	}
	public void getCard(Card newCard) {
		cards.add(newCard);
	}
	public String state() {
		if (point() > 21)
			return "busted";
		if (point() == 21)
			return "blackjack";
		return "";
	}
	public int point() {
		int total = 0;
		int acesCount = 0;
		for (Card c : cards) {
			if (c.faceValue() == 1) {
				total += 11;
				acesCount++;
			}
			else 
				total += ((c.faceValue() > 10)? 10 : c.faceValue());
		}
		//Correct for Aces
		for (int i = 0; i < acesCount; i++) {
			if (total <= 21)
				break;
			total -= 10;
		}
		return total;
	}
}