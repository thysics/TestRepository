"""
Utility functions for statistical arbitrage implementation.
"""

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.stattools import adfuller, coint
from typing import Tuple, List, Dict, Optional, Union


def calculate_sharpe_ratio(returns: np.ndarray, risk_free_rate: float = 0.0) -> float:
    """
    Calculate the annualized Sharpe ratio.
    
    Args:
        returns: Array of returns
        risk_free_rate: Risk-free rate (default: 0)
        
    Returns:
        Annualized Sharpe ratio
    """
    excess_returns = returns - risk_free_rate
    if len(excess_returns) == 0 or np.std(excess_returns) == 0:
        return 0.0
    
    # Assuming daily returns, annualize by multiplying by sqrt(252)
    return np.mean(excess_returns) / np.std(excess_returns) * np.sqrt(252)


def calculate_max_drawdown(cumulative_returns: np.ndarray) -> float:
    """
    Calculate the maximum drawdown.
    
    Args:
        cumulative_returns: Array of cumulative returns
        
    Returns:
        Maximum drawdown as a positive percentage
    """
    if len(cumulative_returns) == 0:
        return 0.0
    
    # Calculate the running maximum
    running_max = np.maximum.accumulate(cumulative_returns)
    
    # Calculate the drawdown
    drawdown = (cumulative_returns - running_max) / running_max
    
    # Return the maximum drawdown as a positive percentage
    return abs(np.min(drawdown))


def test_cointegration(x: np.ndarray, y: np.ndarray, significance_level: float = 0.05) -> Tuple[bool, float]:
    """
    Test for cointegration between two time series using the Engle-Granger two-step method.
    
    Args:
        x: First time series
        y: Second time series
        significance_level: Significance level for the test
        
    Returns:
        Tuple of (is_cointegrated, p_value)
    """
    # Perform cointegration test
    _, p_value, _ = coint(x, y)
    
    # Check if p-value is less than significance level
    is_cointegrated = p_value < significance_level
    
    return is_cointegrated, p_value


def calculate_zscore(spread: np.ndarray, window: int = 20) -> np.ndarray:
    """
    Calculate the z-score of a spread.
    
    Args:
        spread: Spread time series
        window: Rolling window size for mean and std calculation
        
    Returns:
        Z-score time series
    """
    # Calculate rolling mean and standard deviation
    rolling_mean = pd.Series(spread).rolling(window=window).mean().values
    rolling_std = pd.Series(spread).rolling(window=window).std().values
    
    # Calculate z-score
    zscore = np.zeros_like(spread)
    zscore[window:] = (spread[window:] - rolling_mean[window:]) / rolling_std[window:]
    
    return zscore


def plot_pair_trading_results(
    prices1: np.ndarray, 
    prices2: np.ndarray, 
    spread: np.ndarray, 
    zscore: np.ndarray, 
    positions: np.ndarray, 
    cumulative_returns: np.ndarray,
    pair_names: Tuple[str, str] = ('Stock 1', 'Stock 2')
) -> None:
    """
    Plot the results of pair trading.
    
    Args:
        prices1: Price series of first stock
        prices2: Price series of second stock
        spread: Spread between the two stocks
        zscore: Z-score of the spread
        positions: Trading positions
        cumulative_returns: Cumulative returns
        pair_names: Names of the pair stocks
    """
    fig, axes = plt.subplots(4, 1, figsize=(12, 16))
    
    # Plot prices
    axes[0].plot(prices1, label=pair_names[0])
    axes[0].plot(prices2, label=pair_names[1])
    axes[0].set_title('Stock Prices')
    axes[0].legend()
    
    # Plot spread
    axes[1].plot(spread)
    axes[1].set_title('Spread')
    
    # Plot z-score
    axes[2].plot(zscore)
    axes[2].axhline(0, color='black', linestyle='--')
    axes[2].axhline(1.0, color='red', linestyle='--')
    axes[2].axhline(-1.0, color='green', linestyle='--')
    axes[2].axhline(2.0, color='red', linestyle='-.')
    axes[2].axhline(-2.0, color='green', linestyle='-.')
    axes[2].set_title('Z-Score')
    
    # Plot cumulative returns
    axes[3].plot(cumulative_returns)
    axes[3].set_title('Cumulative Returns')
    
    plt.tight_layout()
    plt.savefig('pair_trading_results.png')
    plt.close()