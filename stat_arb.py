import numpy as np
import pandas as pd
from scipy import stats

def generate_mock_data(num_stocks=2, num_days=252, mean_return=0.0001, volatility=0.02):
    """
    Generate mock equity market data for a pair of stocks.
    
    :param num_stocks: Number of stocks to generate data for
    :param num_days: Number of trading days
    :param mean_return: Mean daily return
    :param volatility: Daily volatility
    :return: DataFrame with mock price data
    """
    np.random.seed(42)  # For reproducibility
    
    # Generate returns
    returns = np.random.normal(mean_return, volatility, (num_days, num_stocks))
    
    # Convert returns to prices
    prices = 100 * np.exp(np.cumsum(returns, axis=0))
    
    # Create DataFrame
    date_range = pd.date_range(end=pd.Timestamp.today(), periods=num_days)
    df = pd.DataFrame(prices, index=date_range, columns=[f'Stock_{i+1}' for i in range(num_stocks)])
    
    return df

def calculate_spread(df):
    """
    Calculate the spread between two stocks.
    
    :param df: DataFrame with stock price data
    :return: Series with the spread
    """
    return df['Stock_1'] - df['Stock_2']

def calculate_zscore(spread):
    """
    Calculate the z-score of the spread.
    
    :param spread: Series with the spread
    :return: Series with the z-score
    """
    return (spread - spread.mean()) / spread.std()

def generate_signals(zscore, entry_threshold=1.5, exit_threshold=0.5):
    """
    Generate trading signals based on z-score.
    
    :param zscore: Series with the z-score
    :param entry_threshold: Z-score threshold for entering a position
    :param exit_threshold: Z-score threshold for exiting a position
    :return: Series with trading signals
    """
    signals = pd.Series(index=zscore.index, data=0)
    
    # Long entry
    signals[zscore < -entry_threshold] = 1
    # Short entry
    signals[zscore > entry_threshold] = -1
    
    # Exit long
    signals[(zscore >= -exit_threshold) & (zscore <= exit_threshold) & (signals.shift(1) == 1)] = 0
    # Exit short
    signals[(zscore >= -exit_threshold) & (zscore <= exit_threshold) & (signals.shift(1) == -1)] = 0
    
    return signals

def backtest_strategy(df, signals):
    """
    Backtest the statistical arbitrage strategy.
    
    :param df: DataFrame with stock price data
    :param signals: Series with trading signals
    :return: DataFrame with strategy returns
    """
    # Calculate daily returns
    returns = df.pct_change()
    
    # Calculate strategy returns
    strategy_returns = pd.DataFrame(index=signals.index)
    strategy_returns['Stock_1'] = -signals * returns['Stock_1']
    strategy_returns['Stock_2'] = signals * returns['Stock_2']
    strategy_returns['Total'] = strategy_returns['Stock_1'] + strategy_returns['Stock_2']
    
    return strategy_returns

def evaluate_strategy(strategy_returns):
    """
    Evaluate the performance of the strategy.
    
    :param strategy_returns: DataFrame with strategy returns
    :return: Dict with performance metrics
    """
    total_returns = strategy_returns['Total']
    
    sharpe_ratio = np.sqrt(252) * total_returns.mean() / total_returns.std()
    cumulative_returns = (1 + total_returns).cumprod() - 1
    max_drawdown = (cumulative_returns.cummax() - cumulative_returns).max()
    
    return {
        'Sharpe Ratio': sharpe_ratio,
        'Cumulative Return': cumulative_returns.iloc[-1],
        'Max Drawdown': max_drawdown
    }

def main():
    # Generate mock data
    df = generate_mock_data()
    
    # Calculate spread and z-score
    spread = calculate_spread(df)
    zscore = calculate_zscore(spread)
    
    # Generate signals
    signals = generate_signals(zscore)
    
    # Backtest strategy
    strategy_returns = backtest_strategy(df, signals)
    
    # Evaluate strategy
    performance = evaluate_strategy(strategy_returns)
    
    # Print results
    print("Strategy Performance:")
    for metric, value in performance.items():
        print(f"{metric}: {value:.4f}")

if __name__ == "__main__":
    main()