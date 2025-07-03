import unittest
import pandas as pd
import numpy as np
from stat_arb import (
    generate_mock_data,
    calculate_spread,
    calculate_zscore,
    generate_signals,
    backtest_strategy,
    evaluate_strategy
)

class TestStatArb(unittest.TestCase):
    def setUp(self):
        self.df = generate_mock_data(num_stocks=2, num_days=100)
        self.spread = calculate_spread(self.df)
        self.zscore = calculate_zscore(self.spread)
        self.signals = generate_signals(self.zscore)
        self.strategy_returns = backtest_strategy(self.df, self.signals)
        self.performance = evaluate_strategy(self.strategy_returns)

    def test_generate_mock_data(self):
        self.assertIsInstance(self.df, pd.DataFrame)
        self.assertEqual(self.df.shape, (100, 2))
        self.assertEqual(list(self.df.columns), ['Stock_1', 'Stock_2'])

    def test_calculate_spread(self):
        self.assertIsInstance(self.spread, pd.Series)
        self.assertEqual(len(self.spread), 100)

    def test_calculate_zscore(self):
        self.assertIsInstance(self.zscore, pd.Series)
        self.assertEqual(len(self.zscore), 100)
        self.assertAlmostEqual(self.zscore.mean(), 0, places=2)
        self.assertAlmostEqual(self.zscore.std(), 1, places=2)

    def test_generate_signals(self):
        self.assertIsInstance(self.signals, pd.Series)
        self.assertEqual(len(self.signals), 100)
        self.assertTrue(set(self.signals.unique()).issubset({-1, 0, 1}))

    def test_backtest_strategy(self):
        self.assertIsInstance(self.strategy_returns, pd.DataFrame)
        self.assertEqual(self.strategy_returns.shape, (100, 3))
        self.assertEqual(list(self.strategy_returns.columns), ['Stock_1', 'Stock_2', 'Total'])

    def test_evaluate_strategy(self):
        self.assertIsInstance(self.performance, dict)
        self.assertEqual(set(self.performance.keys()), {'Sharpe Ratio', 'Cumulative Return', 'Max Drawdown'})
        self.assertGreater(self.performance['Sharpe Ratio'], -10)  # Arbitrary threshold
        self.assertLess(self.performance['Sharpe Ratio'], 10)  # Arbitrary threshold
        self.assertGreater(self.performance['Cumulative Return'], -1)  # Can't lose more than 100%
        self.assertGreaterEqual(self.performance['Max Drawdown'], 0)
        self.assertLess(self.performance['Max Drawdown'], 1)

if __name__ == '__main__':
    unittest.main()