# Statistical Arbitrage Alpha Generation

This repository implements a statistical arbitrage strategy for generating alpha in equity markets. The implementation includes synthetic data generation, pair selection, trading strategy, and backtesting framework.

## Overview

Statistical arbitrage is a quantitative trading strategy that seeks to profit from pricing inefficiencies between related securities. This implementation focuses on pairs trading, a common form of statistical arbitrage where two cointegrated securities are traded against each other to capture mean-reverting behavior.

## Features

- **Synthetic Data Generation**: Create realistic equity market data with cointegrated pairs
- **Pair Selection**: Identify cointegrated pairs using statistical tests
- **Trading Strategy**: Implement mean-reversion trading signals based on z-scores
- **Portfolio Management**: Manage a portfolio of multiple pairs
- **Backtesting**: Evaluate strategy performance with comprehensive metrics
- **Visualization**: Generate plots to analyze strategy behavior

## Project Structure

- `data_generation.py`: Generates synthetic market data
- `pair_selection.py`: Identifies cointegrated pairs
- `trading_strategy.py`: Implements the statistical arbitrage strategy
- `backtest.py`: Provides backtesting functionality
- `utils.py`: Contains utility functions
- `main.py`: Entry point for running the full pipeline
- `test_*.py`: Unit tests for each component

## Getting Started

### Prerequisites

- Python 3.7+
- Required packages: numpy, pandas, matplotlib, statsmodels

### Installation

```bash
pip install numpy pandas matplotlib statsmodels
```

### Usage

Run the main script with default parameters:

```bash
python main.py
```

Or customize the parameters:

```bash
python main.py --n_stocks 20 --n_days 1000 --n_cointegrated_pairs 5 --train_ratio 0.7 --entry_threshold 2.0 --exit_threshold 0.5 --max_pairs 5
```

### Running Tests

```bash
python -m unittest discover
```

## Implementation Details

### Data Generation

The `MarketDataGenerator` class creates synthetic equity price data with controlled cointegration relationships between pairs of stocks. This allows for testing the strategy on data with known properties.

### Pair Selection

The `PairSelector` class identifies cointegrated pairs using the Engle-Granger two-step method and calculates key statistics like hedge ratios and mean-reversion half-lives.

### Trading Strategy

The `PairTradingStrategy` class implements a z-score based trading strategy for individual pairs, while the `PortfolioStrategy` class manages a portfolio of multiple pairs.

### Backtesting

The `StatArbBacktester` class provides functionality to backtest the strategy on historical data and calculate performance metrics like Sharpe ratio, maximum drawdown, and win rate.

## Performance Metrics

The strategy's performance is evaluated using the following metrics:

- Total Return
- Annualized Return
- Sharpe Ratio
- Maximum Drawdown
- Win Rate
- Number of Trades

## License

This project is licensed under the MIT License - see the LICENSE file for details.