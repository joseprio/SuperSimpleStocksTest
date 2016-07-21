# Example Assignment – Super Simple Stocks

##Requirements
1. Provide working source code that will :-
  1. For a given stock, 
    1. Given a market price as input, calculate the dividend yield
    2. Given a market price as input,  calculate the P/E Ratio
    3. Record a trade, with timestamp, quantity of shares, buy or sell indicator and trade price
    4. Calculate Volume Weighted Stock Price based on trades in past 15 minutes
  2. Calculate the GBCE All Share Index using the geometric mean of prices for all stocks

##Constraints
1. Written in one of these languages:
  * Java, C#, C++, Python
2. No database or GUI is required, all data need only be held in memory
3. Formulas and data provided are simplified representations for the purpose of this exercise

##Implementation notes
* Requires Java 8
* Use `mvn test` to run the unit tests, `mvn exec:java` to run it (it can be operated via CLI)
* The geometric mean is implemented using logarithms in order to avoid under/overflow calculation problems
* The trade recording is focused on concurrency and speed; a separate DWH service has been implemented in order
to perform the calculations, and data is provided asynchronously in order to complete the trading operations as
quick as possible
* For the market index, the price of a stock is the average of all trades; if there have been no trades, that
stock will not be used to calculate the index
* `double` should have enough precision to handle this system requirements; if additional precision was needed,
we would need to use `BigDecimal` or similar
* The unit test coverage is limited to the requirements; a real implementation would aim 100% coverage
