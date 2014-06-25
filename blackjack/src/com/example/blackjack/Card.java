package com.example.blackjack;
import java.io.*;
import java.util.*;

class Card {
	public int faceIndex, suitIndex;
	public Card(int s, int f) {
		faceIndex = f;
		suitIndex = (s >= 0 || s <= 3)? s : 0;
	}
	public String face() {
		String[] faces = new String[] {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
		return faces[faceIndex];
	}
	public int faceValue() {
		return faceIndex + 1;
	}
	public String suit() {
		String[] suits = new String[] {"clubs", "diamonds", "hearts", "spades"};
		return suits[suitIndex];
	}
	public void print() {
		System.out.println(suit() + " " + face());
	}
}
