"""
Module for implementing statistical arbitrage trading strategies.
"""

import numpy as np
import pandas as pd
from typing import Tuple, List, Dict, Optional
from utils import calculate_zscore


class PairTradingStrategy:
    """
    Class for implementing a pairs trading strategy based on statistical arbitrage.
    """
    
    def __init__(
        self,
        entry_threshold: float = 2.0,
        exit_threshold: float = 0.5,
        stop_loss_threshold: float = 4.0,
        lookback_period: int = 20,
        max_position_days: int = 20
    ):
        """
        Initialize the pairs trading strategy.
        
        Args:
            entry_threshold: Z-score threshold for entering a position
            exit_threshold: Z-score threshold for exiting a position
            stop_loss_threshold: Z-score threshold for stop loss
            lookback_period: Period for calculating z-score
            max_position_days: Maximum number of days to hold a position
        """
        self.entry_threshold = entry_threshold
        self.exit_threshold = exit_threshold
        self.stop_loss_threshold = stop_loss_threshold
        self.lookback_period = lookback_period
        self.max_position_days = max_position_days
        
    def generate_signals(
        self, 
        price1: np.ndarray, 
        price2: np.ndarray, 
        hedge_ratio: float
    ) -> Tuple[np.ndarray, np.ndarray, np.ndarray, np.ndarray]:
        """
        Generate trading signals for a pair of stocks.
        
        Args:
            price1: Price series of first stock
            price2: Price series of second stock
            hedge_ratio: Hedge ratio between the stocks
            
        Returns:
            Tuple of (spread, zscore, position1, position2)
        """
        # Calculate spread
        spread = price2 - hedge_ratio * price1
        
        # Calculate z-score
        zscore = calculate_zscore(spread, window=self.lookback_period)
        
        # Initialize positions
        n = len(price1)
        position1 = np.zeros(n)
        position2 = np.zeros(n)
        
        # Days in current position
        days_in_position = 0
        
        # Generate signals
        for t in range(self.lookback_period, n):
            # If not in a position
            if position1[t-1] == 0 and position2[t-1] == 0:
                # Enter long spread position if z-score is below negative threshold
                if zscore[t] < -self.entry_threshold:
                    position1[t] = 1  # Long stock1
                    position2[t] = -hedge_ratio  # Short stock2
                    days_in_position = 1
                # Enter short spread position if z-score is above positive threshold
                elif zscore[t] > self.entry_threshold:
                    position1[t] = -1  # Short stock1
                    position2[t] = hedge_ratio  # Long stock2
                    days_in_position = 1
            
            # If in a position
            else:
                days_in_position += 1
                
                # Check for exit conditions
                exit_position = False
                
                # Exit if z-score crosses exit threshold
                if position1[t-1] > 0 and zscore[t] > -self.exit_threshold:  # Long spread position
                    exit_position = True
                elif position1[t-1] < 0 and zscore[t] < self.exit_threshold:  # Short spread position
                    exit_position = True
                
                # Exit if stop loss is triggered
                if position1[t-1] > 0 and zscore[t] < -self.stop_loss_threshold:  # Long spread position
                    exit_position = True
                elif position1[t-1] < 0 and zscore[t] > self.stop_loss_threshold:  # Short spread position
                    exit_position = True
                
                # Exit if maximum holding period is reached
                if days_in_position >= self.max_position_days:
                    exit_position = True
                
                if exit_position:
                    position1[t] = 0
                    position2[t] = 0
                    days_in_position = 0
                else:
                    position1[t] = position1[t-1]
                    position2[t] = position2[t-1]
        
        return spread, zscore, position1, position2
    
    def calculate_returns(
        self, 
        price1: np.ndarray, 
        price2: np.ndarray, 
        position1: np.ndarray, 
        position2: np.ndarray
    ) -> Tuple[np.ndarray, np.ndarray]:
        """
        Calculate returns for a pair trading strategy.
        
        Args:
            price1: Price series of first stock
            price2: Price series of second stock
            position1: Position series for first stock
            position2: Position series for second stock
            
        Returns:
            Tuple of (daily_returns, cumulative_returns)
        """
        # Calculate daily price changes
        price_change1 = np.zeros_like(price1)
        price_change2 = np.zeros_like(price2)
        
        price_change1[1:] = (price1[1:] - price1[:-1]) / price1[:-1]
        price_change2[1:] = (price2[1:] - price2[:-1]) / price2[:-1]
        
        # Calculate daily returns from positions
        daily_returns = position1[:-1] * price_change1[1:] + position2[:-1] * price_change2[1:]
        
        # Add a zero at the beginning to match the length of the original arrays
        daily_returns = np.insert(daily_returns, 0, 0)
        
        # Calculate cumulative returns
        cumulative_returns = np.cumprod(1 + daily_returns) - 1
        
        return daily_returns, cumulative_returns


class PortfolioStrategy:
    """
    Class for implementing a portfolio of pair trading strategies.
    """
    
    def __init__(
        self,
        pair_trading_strategy: PairTradingStrategy,
        max_pairs: int = 5,
        capital_per_pair: float = 0.2
    ):
        """
        Initialize the portfolio strategy.
        
        Args:
            pair_trading_strategy: PairTradingStrategy instance
            max_pairs: Maximum number of pairs to trade simultaneously
            capital_per_pair: Fraction of capital allocated to each pair
        """
        self.pair_trading_strategy = pair_trading_strategy
        self.max_pairs = max_pairs
        self.capital_per_pair = capital_per_pair
        
    def backtest(
        self, 
        price_data: pd.DataFrame, 
        pairs: List[Tuple[str, str]], 
        hedge_ratios: Dict[str, float]
    ) -> Tuple[pd.DataFrame, Dict]:
        """
        Backtest the portfolio strategy.
        
        Args:
            price_data: DataFrame with stock prices
            pairs: List of stock pairs to trade
            hedge_ratios: Dictionary with hedge ratios for each pair
            
        Returns:
            Tuple of (returns_df, performance_metrics)
        """
        # Limit the number of pairs
        pairs = pairs[:self.max_pairs]
        
        # Initialize returns DataFrame
        returns_df = pd.DataFrame(index=price_data.index)
        returns_df['portfolio_return'] = 0.0
        
        # Dictionary to store pair trading results
        pair_results = {}
        
        # Backtest each pair
        for i, (stock1, stock2) in enumerate(pairs):
            # Get price series
            price1 = price_data[stock1].values
            price2 = price_data[stock2].values
            
            # Get hedge ratio
            pair_key = f"{stock1}_{stock2}"
            hedge_ratio = hedge_ratios.get(pair_key, 1.0)
            
            # Generate signals
            spread, zscore, position1, position2 = self.pair_trading_strategy.generate_signals(
                price1, price2, hedge_ratio
            )
            
            # Calculate returns
            daily_returns, cumulative_returns = self.pair_trading_strategy.calculate_returns(
                price1, price2, position1, position2
            )
            
            # Add to portfolio returns with equal weighting
            returns_df[f'pair_{i+1}_return'] = daily_returns
            returns_df['portfolio_return'] += daily_returns * self.capital_per_pair
            
            # Store results
            pair_results[f"{stock1}_{stock2}"] = {
                'spread': spread,
                'zscore': zscore,
                'position1': position1,
                'position2': position2,
                'daily_returns': daily_returns,
                'cumulative_returns': cumulative_returns
            }
        
        # Calculate cumulative portfolio returns
        returns_df['portfolio_cumulative_return'] = (1 + returns_df['portfolio_return']).cumprod() - 1
        
        # Calculate performance metrics
        performance_metrics = self._calculate_performance_metrics(returns_df['portfolio_return'].values)
        
        return returns_df, performance_metrics, pair_results
    
    def _calculate_performance_metrics(self, returns: np.ndarray) -> Dict:
        """
        Calculate performance metrics for the strategy.
        
        Args:
            returns: Array of returns
            
        Returns:
            Dictionary with performance metrics
        """
        from utils import calculate_sharpe_ratio, calculate_max_drawdown
        
        # Calculate cumulative returns
        cumulative_returns = (1 + returns).cumprod() - 1
        
        # Calculate metrics
        total_return = cumulative_returns[-1]
        sharpe_ratio = calculate_sharpe_ratio(returns)
        max_drawdown = calculate_max_drawdown(cumulative_returns)
        
        # Calculate annualized return (assuming daily returns)
        n_days = len(returns)
        annualized_return = (1 + total_return) ** (252 / n_days) - 1
        
        # Calculate win rate
        trades = np.diff(np.where(returns != 0, 1, 0))
        trade_starts = np.where(trades == 1)[0] + 1
        trade_ends = np.where(trades == -1)[0] + 1
        
        if len(trade_starts) > len(trade_ends):
            trade_ends = np.append(trade_ends, len(returns) - 1)
        
        trade_returns = []
        for start, end in zip(trade_starts, trade_ends):
            trade_return = (1 + returns[start:end+1]).prod() - 1
            trade_returns.append(trade_return)
        
        win_rate = np.mean([r > 0 for r in trade_returns]) if trade_returns else 0
        
        return {
            'total_return': total_return,
            'annualized_return': annualized_return,
            'sharpe_ratio': sharpe_ratio,
            'max_drawdown': max_drawdown,
            'win_rate': win_rate,
            'num_trades': len(trade_returns)
        }


if __name__ == "__main__":
    # Example usage
    from data_generation import MarketDataGenerator
    from pair_selection import PairSelector
    
    # Generate synthetic data
    generator = MarketDataGenerator(n_stocks=10, n_days=500, n_cointegrated_pairs=3, seed=42)
    data = generator.generate_data()
    
    # Find cointegrated pairs
    selector = PairSelector()
    pairs = selector.find_cointegrated_pairs(data)
    
    # Get hedge ratios
    hedge_ratios = {f"{stock1}_{stock2}": selector.pair_stats[f"{stock1}_{stock2}"]["hedge_ratio"] 
                   for stock1, stock2 in pairs}
    
    # Create trading strategy
    pair_strategy = PairTradingStrategy()
    portfolio_strategy = PortfolioStrategy(pair_strategy)
    
    # Backtest
    returns_df, metrics, pair_results = portfolio_strategy.backtest(data, pairs, hedge_ratios)
    
    # Print results
    print("Performance Metrics:")
    for metric, value in metrics.items():
        print(f"{metric}: {value:.4f}")