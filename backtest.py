"""
Module for backtesting statistical arbitrage strategies.
"""

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from typing import Tuple, List, Dict, Optional
from data_generation import MarketDataGenerator, split_data
from pair_selection import PairSelector
from trading_strategy import PairTradingStrategy, PortfolioStrategy
from utils import plot_pair_trading_results


class StatArbBacktester:
    """
    Class for backtesting statistical arbitrage strategies.
    """
    
    def __init__(
        self,
        pair_selector: PairSelector,
        portfolio_strategy: PortfolioStrategy
    ):
        """
        Initialize the backtester.
        
        Args:
            pair_selector: PairSelector instance
            portfolio_strategy: PortfolioStrategy instance
        """
        self.pair_selector = pair_selector
        self.portfolio_strategy = portfolio_strategy
        
    def run_backtest(
        self, 
        train_data: pd.DataFrame, 
        test_data: pd.DataFrame
    ) -> Tuple[pd.DataFrame, Dict, Dict]:
        """
        Run a backtest on the given data.
        
        Args:
            train_data: Training data for pair selection
            test_data: Testing data for backtesting
            
        Returns:
            Tuple of (returns_df, performance_metrics, pair_results)
        """
        # Find cointegrated pairs on training data
        pairs = self.pair_selector.find_cointegrated_pairs(train_data)
        
        if not pairs:
            raise ValueError("No cointegrated pairs found in the training data")
        
        # Get hedge ratios
        hedge_ratios = {f"{stock1}_{stock2}": self.pair_selector.pair_stats[f"{stock1}_{stock2}"]["hedge_ratio"] 
                       for stock1, stock2 in pairs}
        
        # Run backtest on test data
        returns_df, metrics, pair_results = self.portfolio_strategy.backtest(
            test_data, pairs, hedge_ratios
        )
        
        return returns_df, metrics, pair_results
    
    def plot_results(
        self, 
        returns_df: pd.DataFrame, 
        metrics: Dict,
        pair_results: Dict,
        plot_pairs: bool = True
    ) -> None:
        """
        Plot the results of the backtest.
        
        Args:
            returns_df: DataFrame with returns
            metrics: Dictionary with performance metrics
            pair_results: Dictionary with pair trading results
            plot_pairs: Whether to plot individual pair results
        """
        # Plot portfolio returns
        plt.figure(figsize=(12, 6))
        plt.plot(returns_df['portfolio_cumulative_return'])
        plt.title('Portfolio Cumulative Returns')
        plt.xlabel('Date')
        plt.ylabel('Cumulative Return')
        plt.grid(True)
        plt.savefig('portfolio_returns.png')
        plt.close()
        
        # Print performance metrics
        print("Performance Metrics:")
        for metric, value in metrics.items():
            if isinstance(value, float):
                print(f"{metric}: {value:.4f}")
            else:
                print(f"{metric}: {value}")
        
        # Plot individual pair results if requested
        if plot_pairs:
            for pair_name, result in pair_results.items():
                stock1, stock2 = pair_name.split('_')
                
                # Extract data
                spread = result['spread']
                zscore = result['zscore']
                position1 = result['position1']
                cumulative_returns = result['cumulative_returns']
                
                # Get prices from the pair name
                prices1 = test_data[stock1].values
                prices2 = test_data[stock2].values
                
                # Plot results
                plot_pair_trading_results(
                    prices1, prices2, spread, zscore, position1, cumulative_returns,
                    pair_names=(stock1, stock2)
                )


def run_full_backtest(
    n_stocks: int = 20,
    n_days: int = 1000,
    n_cointegrated_pairs: int = 5,
    train_ratio: float = 0.7,
    seed: Optional[int] = 42
) -> Tuple[pd.DataFrame, Dict, Dict]:
    """
    Run a full backtest with synthetic data.
    
    Args:
        n_stocks: Number of stocks to generate
        n_days: Number of trading days
        n_cointegrated_pairs: Number of cointegrated pairs to create
        train_ratio: Ratio of data to use for training
        seed: Random seed for reproducibility
        
    Returns:
        Tuple of (returns_df, performance_metrics, pair_results)
    """
    # Generate synthetic data
    generator = MarketDataGenerator(
        n_stocks=n_stocks, 
        n_days=n_days, 
        n_cointegrated_pairs=n_cointegrated_pairs, 
        seed=seed
    )
    data = generator.generate_data()
    
    # Split data
    train_data, test_data = split_data(data, train_ratio=train_ratio)
    
    # Create components
    pair_selector = PairSelector()
    pair_strategy = PairTradingStrategy()
    portfolio_strategy = PortfolioStrategy(pair_strategy)
    
    # Create backtester
    backtester = StatArbBacktester(pair_selector, portfolio_strategy)
    
    # Run backtest
    returns_df, metrics, pair_results = backtester.run_backtest(train_data, test_data)
    
    # Plot results
    backtester.plot_results(returns_df, metrics, pair_results)
    
    return returns_df, metrics, pair_results


if __name__ == "__main__":
    # Run a full backtest
    returns_df, metrics, pair_results = run_full_backtest(
        n_stocks=20,
        n_days=1000,
        n_cointegrated_pairs=5,
        train_ratio=0.7,
        seed=42
    )