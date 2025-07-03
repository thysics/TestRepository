# Statistical Arbitrage Alpha Generation

This repository contains a simple implementation of a statistical arbitrage strategy for generating alpha in equity markets. The strategy is based on pairs trading, where we exploit mean-reversion in the spread between two correlated stocks.

## Contents

1. `stat_arb.py`: Main implementation of the statistical arbitrage strategy
2. `test_stat_arb.py`: Unit tests for the strategy implementation

## Features

- Mock equity market data generation
- Spread and z-score calculation
- Signal generation based on z-score thresholds
- Strategy backtesting
- Performance evaluation (Sharpe ratio, cumulative return, max drawdown)

## Usage

To run the strategy:

```
python stat_arb.py
```

To run the tests:

```
python -m unittest test_stat_arb.py
```

## Note

This is a simplified implementation for educational purposes. Real-world statistical arbitrage strategies would require more sophisticated analysis, risk management, and transaction cost considerations.