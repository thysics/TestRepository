"""
Main entry point for the statistical arbitrage project.
"""

import argparse
import pandas as pd
import matplotlib.pyplot as plt
from data_generation import MarketDataGenerator, split_data
from pair_selection import PairSelector
from trading_strategy import PairTradingStrategy, PortfolioStrategy
from backtest import StatArbBacktester, run_full_backtest


def parse_arguments():
    """
    Parse command line arguments.
    
    Returns:
        Parsed arguments
    """
    parser = argparse.ArgumentParser(description='Statistical Arbitrage')
    
    parser.add_argument('--n_stocks', type=int, default=20,
                        help='Number of stocks to generate')
    parser.add_argument('--n_days', type=int, default=1000,
                        help='Number of trading days')
    parser.add_argument('--n_cointegrated_pairs', type=int, default=5,
                        help='Number of cointegrated pairs to create')
    parser.add_argument('--train_ratio', type=float, default=0.7,
                        help='Ratio of data to use for training')
    parser.add_argument('--seed', type=int, default=42,
                        help='Random seed for reproducibility')
    parser.add_argument('--entry_threshold', type=float, default=2.0,
                        help='Z-score threshold for entering a position')
    parser.add_argument('--exit_threshold', type=float, default=0.5,
                        help='Z-score threshold for exiting a position')
    parser.add_argument('--max_pairs', type=int, default=5,
                        help='Maximum number of pairs to trade simultaneously')
    
    return parser.parse_args()


def main():
    """
    Main function.
    """
    # Parse arguments
    args = parse_arguments()
    
    print("Statistical Arbitrage Backtest")
    print("==============================")
    print(f"Number of stocks: {args.n_stocks}")
    print(f"Number of days: {args.n_days}")
    print(f"Number of cointegrated pairs: {args.n_cointegrated_pairs}")
    print(f"Train ratio: {args.train_ratio}")
    print(f"Entry threshold: {args.entry_threshold}")
    print(f"Exit threshold: {args.exit_threshold}")
    print(f"Max pairs: {args.max_pairs}")
    print("==============================")
    
    # Generate synthetic data
    print("Generating synthetic market data...")
    generator = MarketDataGenerator(
        n_stocks=args.n_stocks, 
        n_days=args.n_days, 
        n_cointegrated_pairs=args.n_cointegrated_pairs, 
        seed=args.seed
    )
    data = generator.generate_data()
    
    # Split data
    train_data, test_data = split_data(data, train_ratio=args.train_ratio)
    print(f"Training data shape: {train_data.shape}")
    print(f"Testing data shape: {test_data.shape}")
    
    # Create components
    pair_selector = PairSelector()
    pair_strategy = PairTradingStrategy(
        entry_threshold=args.entry_threshold,
        exit_threshold=args.exit_threshold
    )
    portfolio_strategy = PortfolioStrategy(
        pair_trading_strategy=pair_strategy,
        max_pairs=args.max_pairs
    )
    
    # Create backtester
    backtester = StatArbBacktester(pair_selector, portfolio_strategy)
    
    # Run backtest
    print("Running backtest...")
    returns_df, metrics, pair_results = backtester.run_backtest(train_data, test_data)
    
    # Print results
    print("\nPerformance Metrics:")
    for metric, value in metrics.items():
        if isinstance(value, float):
            print(f"{metric}: {value:.4f}")
        else:
            print(f"{metric}: {value}")
    
    # Plot results
    print("\nPlotting results...")
    backtester.plot_results(returns_df, metrics, pair_results)
    
    print("\nBacktest completed successfully!")


if __name__ == "__main__":
    main()