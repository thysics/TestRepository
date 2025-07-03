"""
Module for generating synthetic market data for testing statistical arbitrage strategies.
"""

import numpy as np
import pandas as pd
from typing import Tuple, List, Dict, Optional


class MarketDataGenerator:
    """
    Class for generating synthetic market data with cointegrated pairs.
    """
    
    def __init__(
        self, 
        n_stocks: int = 20, 
        n_days: int = 1000, 
        n_cointegrated_pairs: int = 5,
        seed: Optional[int] = None
    ):
        """
        Initialize the market data generator.
        
        Args:
            n_stocks: Number of stocks to generate
            n_days: Number of trading days
            n_cointegrated_pairs: Number of cointegrated pairs to create
            seed: Random seed for reproducibility
        """
        self.n_stocks = n_stocks
        self.n_days = n_days
        self.n_cointegrated_pairs = min(n_cointegrated_pairs, n_stocks // 2)
        
        if seed is not None:
            np.random.seed(seed)
            
        self.stock_names = [f"STOCK_{i}" for i in range(1, n_stocks + 1)]
        self.cointegrated_pairs = []
        
    def generate_data(self) -> pd.DataFrame:
        """
        Generate synthetic market data with cointegrated pairs.
        
        Returns:
            DataFrame with stock prices
        """
        # Initialize price matrix
        prices = np.zeros((self.n_days, self.n_stocks))
        
        # Generate random walk prices for non-cointegrated stocks
        for i in range(self.n_stocks):
            # Start with a random price between 10 and 100
            prices[0, i] = np.random.uniform(10, 100)
            
            # Generate daily returns with drift and volatility
            daily_returns = np.random.normal(0.0001, 0.01, self.n_days - 1)
            
            # Calculate prices using cumulative returns
            for t in range(1, self.n_days):
                prices[t, i] = prices[t-1, i] * (1 + daily_returns[t-1])
        
        # Create cointegrated pairs
        self.cointegrated_pairs = []
        for i in range(self.n_cointegrated_pairs):
            # Select two random stocks
            idx1 = i * 2
            idx2 = i * 2 + 1
            
            # Store the pair
            self.cointegrated_pairs.append((idx1, idx2))
            
            # Make them cointegrated by setting one as a scaled version of the other plus noise
            cointegration_factor = np.random.uniform(0.5, 1.5)
            mean_reverting_factor = np.random.uniform(0.02, 0.05)  # Speed of mean reversion
            
            # Initialize the spread as a mean-reverting process
            spread = np.zeros(self.n_days)
            spread[0] = np.random.normal(0, 1)
            
            for t in range(1, self.n_days):
                # Ornstein-Uhlenbeck process for the spread
                spread[t] = spread[t-1] - mean_reverting_factor * spread[t-1] + np.random.normal(0, 0.1)
                
                # Adjust the second stock to maintain the cointegration relationship
                prices[t, idx2] = cointegration_factor * prices[t, idx1] + spread[t]
        
        # Create DataFrame
        df = pd.DataFrame(prices, columns=self.stock_names)
        
        # Add date index
        end_date = pd.Timestamp.now().floor('D')
        start_date = end_date - pd.Timedelta(days=self.n_days - 1)
        date_range = pd.date_range(start=start_date, end=end_date, periods=self.n_days)
        df.index = date_range
        
        return df
    
    def get_cointegrated_pairs(self) -> List[Tuple[int, int]]:
        """
        Get the list of cointegrated pairs.
        
        Returns:
            List of tuples with indices of cointegrated stocks
        """
        return [(self.stock_names[i], self.stock_names[j]) for i, j in self.cointegrated_pairs]


def split_data(df: pd.DataFrame, train_ratio: float = 0.7) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """
    Split data into training and testing sets.
    
    Args:
        df: DataFrame with stock prices
        train_ratio: Ratio of data to use for training
        
    Returns:
        Tuple of (train_df, test_df)
    """
    train_size = int(len(df) * train_ratio)
    train_df = df.iloc[:train_size]
    test_df = df.iloc[train_size:]
    return train_df, test_df


if __name__ == "__main__":
    # Example usage
    generator = MarketDataGenerator(n_stocks=10, n_days=500, n_cointegrated_pairs=3, seed=42)
    data = generator.generate_data()
    print(data.head())
    print(f"Cointegrated pairs: {generator.get_cointegrated_pairs()}")
    
    # Split data
    train_data, test_data = split_data(data)
    print(f"Training data shape: {train_data.shape}")
    print(f"Testing data shape: {test_data.shape}")