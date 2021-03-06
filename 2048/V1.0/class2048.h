/**********************

Copyright : Jancoyan

Auther : Jancoyan

Date : 2020/04/12

Discription : 2048 Game

***********************/
#pragma once
class GameBody {
public:
	GameBody();

	void PrintMainBody(); //print the surface

	void addCard(); //add card 2 or 4

	void moveCard(); //move card

	void IsOver(); //judge status

	bool isFull(); //is it all 0 ?

	void YouLose();// print you failed
		
	void YouWin(); // print you win

public:
	int m_Score;
	int m_GameStatus;
	int m_Layout[4][4];
};