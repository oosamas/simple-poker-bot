
import java.util.*;
import java.text.*;

public class PokerBot implements PLBadugiPlayer
  {
    private int lastBetWasBluff = -1;
    private int lastDrawWasBluff = -1;
    private int weFoldedToRaise = -1;
    private int weRaisedLast = -1;
    private int ourLastDraw = 0;
    private int count = 0; // counter of objects created
    private String name; //name of Agent-- Badugi Newbie
    private int[] handCount; // # of rounds in 4 rounds of hands
    private int[] opponentAggro = new int[4]; //opponent aggressiveness tracker at each round
    private int[] ourAggro = new int[4]; //our aggressivenes tracker at each round
    private double chances = 1.0;
    private double[] drawMult = {1.1, 1.3, 1.5, 1.9};
    private int position;
    private Random rng = new Random();
    private static final double MAXAGGRO = 5;




    public PokerBot(String name)
    {
      this.name = name;
    }
    public PLBadugi500458829()
    {
      this.name = "Badugi ShereKhan Newbie" + (++count);
    }
    /**
     * The method to inform the agent that a new heads-up match is starting.
     * @param handsToGo How many hands this tournament consists of.
     */
    public void startNewMatch(int handsToGo)
    {
      handCount = new int[4];
      opponentAggro[0] = opponentAggro[1] = opponentAggro[2] = opponentAggro[3] = 0;
      ourAggro[0] = ourAggro[1] = ourAggro[2] = ourAggro[3] = 0;
    }

    /**
     * The method to inform the agent that the current heads-up match has ended.
     * @param finalScore The total number of chips accumulated by this player during the match.
     */
    public void finishedMatch(int finalScore)
    {

    }

    /**
     * The method to inform the agent that a new hand is starting.
     * @param position 0 if the agent is the dealer in this hand, 1 if the opponent.
     * @param handsToGo The number of hands left to play in this heads-up tournament.
     * @param currentScore The current score of the tournament.
     */
     public void startNewHand(int position, int handsToGo, int currentScore)
    {
      lastBetWasBluff = -1;
      lastDrawWasBluff = -1;
      weFoldedToRaise = -1;
      weRaisedLast = -1;
      chances = 1.0;
      this.position = position;
    }

    /**
     * The method to ask the agent what betting action it wants to perform.
     * @param drawsRemaining How many draws are remaining after this betting round.
     * @param hand The current hand held by this player.
     * @param pot The current size of the pot.
     * @param raises The number of raises made in this round.
     * @param toCall The cost to call to stay in the pot.
     * @param minRaise The minimum allowed raise to make, if the agent wants to raise.
     * @param maxRaise The maximum allowed raise to make, if the agent wants to raise.
     * @param opponentDrew How many cards the opponent drew in the previous drawing round. In the
     * first betting round, this argument will be -1.
     * @return The amount of chips that the player pushes into the pot. Putting in less than
     * toCall means folding. Any amount less than minRaise becomes a call, and any amount between
     * minRaise and maxRaise, inclusive, is a raise. Any amount greater than maxRaise is clipped at
     * maxRaise.
     */
    public int bettingAction(int drawsRemaining, PLBadugiHand hand, int pot,
            int raises, int toCall, int minRaise, int maxRaise, int opponentDrew)
    {
      int activeCardAmount = hand.getActiveRanks().length;
      int topCard = hand.getActiveRanks()[1];

      if (activeCardAmount == 4) //start at the top
      {
          if (drawsRemaining == 3) //we just started
          {
              if (position == 0) // 1st
              {
                  if (toCall <= 0 && topCard <= 8)
                  {
                      return maxRaise-minRaise; // random bet
                  }
                  else
                  {
                    return toCall;
                  }
              }
              if (position == 0)
              {
                  if (toCall >= 0 && topCard <= 6)
                  {
                      return maxRaise;
                  }
                  else
                  {
                      return toCall;
                  }
              }
              if (position == 1) // as 2nd
              {
                  if(toCall >= 0 && topCard <=6)
                  {
                      return maxRaise; // take a chance go bold
                  }
                  else
                  {
                      return toCall; // but also just sty in
                  }
              }
          }

          if(drawsRemaining == 2) //2nd round
          {
              if (position == 0)
              {
                  if (opponentDrew > 0 && toCall <= 0 && topCard <= 8)
                  {
                      return 0;
                  }
                  else
                  {
                      return toCall; //change tactic from maxRaise
                  }
              }
              if (position == 1)
              {
                  if (opponentDrew <= 0 && toCall >= 0 && topCard <= 6)
                  {
                      return toCall;
                  }
                  else
                  {
                      return maxRaise;
                  }
              }
          }

          if (drawsRemaining == 1) // 3rd round
          {
              if (position == 0)
              {
                  if (toCall <= 0 && topCard >= 9)
                  {
                    return toCall;
                  }
                  else
                  {
                    return maxRaise;
                  }
              }
              if (position == 1)
              {
                  if (toCall >= 0 && topCard <= 6)
                  {
                    return maxRaise;
                  }
                  if (toCall >=0 && ourLastDraw < opponentDrew)
                  {
                      return minRaise;
                  }
                  /*else
                  {
                    return 0;
                  }*/
              }
          }

          if(drawsRemaining==0) //final countdown at 4 badgui
          {
              if (opponentDrew <= 0)
              {
                  // opponenet good hand
                  if (toCall > 0)
                  {
                      if (topCard > 10)// 10 a 2 3
                      {
                        return 0;
                      }
                      if (topCard <= 10)
                      {
                        return toCall;
                      }
                      if (topCard < 6)
                      {
                        return minRaise; //still unsure how we stack up
                      }
                  }

                  //No draw, no bet
                  if(toCall <= 0)
                  {
                      return toCall;
                  }
              }
              if (opponentDrew > 0)
              {
                  if (toCall <= 0)
                  {
                      if (topCard >= 9 && ourLastDraw < opponentDrew)
                      {
                        return toCall; //lets take it to showdown-dont fold
                      }
                      if (topCard < 9)
                      {
                        return maxRaise;
                      }
                  }

                  if (toCall > 0)
                  {
                      //if (topCard >= 6)
                     // {
                       // return 0;
                     // }
                      if (topCard < 6)
                      {
                        return maxRaise;
                      }
                      else return toCall; // to far to call it quits
                  }
              }
          }
      }
      if (activeCardAmount == 3) //frequent folding here
      {
          if (drawsRemaining == 3)
          {
              if ((position == 0 && topCard <= 6))
              {
                return maxRaise;
              }
              if (toCall > 0 && topCard <= 6)
              {
                return maxRaise;
              }
              if (toCall > 0 && topCard > 6)
              {
                return toCall;
              }
          }
          if (drawsRemaining == 2) //*** */
          {

              if (toCall > 0 && topCard < 5) //bug from 7, but keeps folding too soon
              {
                return maxRaise;
              }
              if (toCall > 0 && opponentDrew >= 1)
              {
                return maxRaise-minRaise;
              }
              else return toCall; //added else
          }
          if (drawsRemaining == 1) //
          {
              if (opponentDrew >= 1 && toCall <= 0)
              {
                return maxRaise-minRaise;//+1;
              }
              if (ourLastDraw < opponentDrew || topCard < 7)//(toCall > 0 || topCard > 7)
              {
                return minRaise; //nth iteration
              }
              return toCall; //from folding at 0
          }
          /*if (drawsRemaining == 0)
          {
              if (toCall > 0) return -1;
              return 0;
          }*/
      }
      while (activeCardAmount <= 2) //make it or break it;
      {
          if (drawsRemaining == 3)
          {
              if (position == 0 && toCall <= 0)
              {
                return toCall;
              }
              if (opponentDrew == 0)
              {
                  return -1; // run for the cover
              }
              else return minRaise; //added else+1
          }
          if (drawsRemaining == 2) // at 980,000
          {
              if (opponentDrew >=2)// && toCall<= 0)
              {
                return toCall;
              }
              if (ourLastDraw < opponentDrew)
              {
                  return minRaise;
              }
              else
              {
                return -1; //cut the cord, minimize losses, bet on the next game
              }
          }
          if (drawsRemaining == 1) //least odds
          {
             if (ourLastDraw < opponentDrew && toCall <=0) // >= 1 && toCall <= 0)
             {
               return minRaise;
             }
              else return -1;
          }
          if (drawsRemaining == 0)
          {
              if(ourLastDraw < opponentDrew)// || toCall <= 0)//> 0 || toCall <= 0)
              {
                return minRaise;
              }
              else{
                return -1;
              }
          }
      }
      return 0;
  }


    /**
     * The method to ask the agent which cards it wants to replace in this drawing round.
     * @param drawsRemaining How many draws are remaining, including this drawing round.
     * @param hand The current hand held by this player.
     * @param pot The current size of the pot.
     * @param dealerDrew How many cards the dealer drew in this drawing round. When this method is called
     * for the dealer, this argument will be -1.
     * @return The list of cards in the hand that the agent wants to replace.
     */
    public List<Card> drawingAction(int drawsRemaining, PLBadugiHand hand, int pot, int dealerDrew)
    {

      int highestCard = 0;
      List<Card> drawCards = new ArrayList<Card>();
      List<Card> allHand =  hand.getAllCards();
      PLBadugiHand currentHand = hand;
      boolean highHand = true;

      if (hand.getActiveCards().size() == 4 && allHand.get(1).getRank() <= 4)
        {
          if (hand.getActiveCards().size() == 4 && allHand.get(1).getRank() <= 6)
            {
              highHand = false;
            }
        }

        allHand = hand.getAllCards();
        if (drawsRemaining >= 2 && hand.getActiveCards().size() == 4 && highHand == false)
          {
            for(Card card :allHand)
              {
                int tempRank = card.getRank();
                if (tempRank > highestCard)
                {
                    drawCards.add(0,card);
                    highestCard = tempRank;
                }
              }
            if (highestCard > 12) return drawCards;
            else
              {
                drawCards = new ArrayList<Card>();
                return drawCards;
              }
          }
          drawCards = hand.getInactiveCards();
          return drawCards;

    }

    /**
     * The method that gets called at the end of the current hand, whether fold or showdown.
     * @param yourHand The hand held by this agent.
     * @param opponentHand The hand held by the opponent, or null if either player folded.
     * @param result The win or the loss in chips for the player.
     */
    public void handComplete(PLBadugiHand yourHand, PLBadugiHand opponentHand, int result)
    {}

    /**
     * Returns the nickname of this agent.
     * @return The nickname of this agent.
     */
    public String getAgentName()
    {
      return name;
    }
    /**
     * Returns the author of this agent. The name should be given in the format "Last, First".
     * @return The author of this agent.
     */
    public String getAuthor()
    {
      return "Saddique, Osama";
    }
  }
