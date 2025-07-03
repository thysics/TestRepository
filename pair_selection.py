"""
Module for selecting cointegrated pairs for statistical arbitrage.
"""

import numpy as np
import pandas as pd
from itertools import combinations
from typing import List, Tuple, Dict, Optional
from utils import test_cointegration


class PairSelector:
    """
    Class for selecting cointegrated pairs from a set of stocks.
    """
    
    def __init__(
        self, 
        significance_level: float = 0.05,
        min_half_life: int = 5,
        max_half_life: int = 100
    ):
        """
        Initialize the pair selector.
        
        Args:
            significance_level: Significance level for cointegration test
            min_half_life: Minimum half-life for mean reversion (in days)
            max_half_life: Maximum half-life for mean reversion (in days)
        """
        self.significance_level = significance_level
        self.min_half_life = min_half_life
        self.max_half_life = max_half_life
        self.pairs = []
        self.pair_stats = {}
        
    def find_cointegrated_pairs(self, price_data: pd.DataFrame) -> List[Tuple[str, str]]:
        """
        Find cointegrated pairs in the price data.
        
        Args:
            price_data: DataFrame with stock prices
            
        Returns:
            List of tuples with stock pairs that are cointegrated
        """
        n = len(price_data.columns)
        self.pairs = []
        self.pair_stats = {}
        
        # Get all possible pairs
        stock_pairs = list(combinations(price_data.columns, 2))
        
        for pair in stock_pairs:
            stock1, stock2 = pair
            
            # Get price series
            price1 = price_data[stock1].values
            price2 = price_data[stock2].values
            
            # Test for cointegration
            is_cointegrated, p_value = test_cointegration(price1, price2, self.significance_level)
            
            if is_cointegrated:
                # Calculate hedge ratio using OLS
                hedge_ratio = np.polyfit(price1, price2, 1)[0]
                
                # Calculate spread
                spread = price2 - hedge_ratio * price1
                
                # Calculate half-life of mean reversion
                half_life = self._calculate_half_life(spread)
                
                # Check if half-life is within acceptable range
                if self.min_half_life <= half_life <= self.max_half_life:
                    self.pairs.append((stock1, stock2))
                    self.pair_stats[f"{stock1}_{stock2}"] = {
                        "p_value": p_value,
                        "hedge_ratio": hedge_ratio,
                        "half_life": half_life
                    }
        
        return self.pairs
    
    def _calculate_half_life(self, spread: np.ndarray) -> float:
        """
        Calculate the half-life of mean reversion for a spread.
        
        Args:
            spread: Spread time series
            
        Returns:
            Half-life in days
        """
        # Calculate lag-1 spread
        lag_spread = np.roll(spread, 1)[1:]
        spread = spread[1:]
        
        # Calculate the change in spread
        delta_spread = spread - lag_spread
        
        # Fit AR(1) model: delta_spread = gamma * lag_spread + epsilon
        gamma = np.polyfit(lag_spread, delta_spread, 1)[0]
        
        # Calculate half-life: ln(2) / ln(1 - gamma)
        if gamma < 0:
            half_life = np.log(2) / np.log(1 + gamma)
        else:
            # If gamma is positive, the process is not mean-reverting
            half_life = np.inf
            
        return half_life
    
    def get_pair_stats(self) -> Dict:
        """
        Get statistics for the selected pairs.
        
        Returns:
            Dictionary with pair statistics
        """
        return self.pair_stats
    
    def get_hedge_ratio(self, stock1: str, stock2: str) -> float:
        """
        Get the hedge ratio for a pair of stocks.
        
        Args:
            stock1: First stock
            stock2: Second stock
            
        Returns:
            Hedge ratio
        """
        key = f"{stock1}_{stock2}"
        if key in self.pair_stats:
            return self.pair_stats[key]["hedge_ratio"]
        
        # Try the reverse order
        key = f"{stock2}_{stock1}"
        if key in self.pair_stats:
            # Inverse of the hedge ratio
            return 1.0 / self.pair_stats[key]["hedge_ratio"]
        
        # If pair not found, calculate it on the fly
        raise ValueError(f"Pair {stock1}_{stock2} not found in pair statistics")


if __name__ == "__main__":
    # Example usage
    from data_generation import MarketDataGenerator
    
    # Generate synthetic data
    generator = MarketDataGenerator(n_stocks=10, n_days=500, n_cointegrated_pairs=3, seed=42)
    data = generator.generate_data()
    
    # Find cointegrated pairs
    selector = PairSelector()
    pairs = selector.find_cointegrated_pairs(data)
    
    print(f"Found {len(pairs)} cointegrated pairs:")
    for pair in pairs:
        stock1, stock2 = pair
        stats = selector.pair_stats[f"{stock1}_{stock2}"]
        print(f"{stock1} - {stock2}: p-value={stats['p_value']:.4f}, "
              f"hedge_ratio={stats['hedge_ratio']:.4f}, half_life={stats['half_life']:.2f} days")