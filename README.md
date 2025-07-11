# Sentiment Analysis with PyTorch

A lightweight sentiment analysis implementation using a shallow neural network built with PyTorch.

## Overview

This repository contains a simple yet effective sentiment analysis model that classifies text as positive or negative sentiment. The implementation focuses on simplicity and educational value, using a relatively shallow neural network architecture.

## Features

- **Shallow Neural Network**: Efficient 2-3 layer architecture for fast training and inference
- **PyTorch Implementation**: Built using PyTorch for flexibility and ease of use
- **Simulated Training Data**: Includes generated examples for training and testing
- **Text Preprocessing**: Basic tokenization and vectorization pipeline
- **Model Evaluation**: Performance metrics and validation tools

## Requirements

```
torch>=1.9.0
numpy>=1.21.0
scikit-learn>=1.0.0
matplotlib>=3.5.0
```

## Project Structure

```
├── README.md
├── requirements.txt
├── src/
│   ├── model.py          # Neural network architecture
│   ├── data_loader.py    # Data preprocessing and loading
│   ├── train.py          # Training script
│   └── evaluate.py       # Model evaluation
├── data/
│   ├── simulated_data.py # Generate training examples
│   └── sample_data.txt   # Sample sentiment data
└── examples/
    └── demo.py           # Usage demonstration
```

## Model Architecture

The shallow neural network consists of:
- **Input Layer**: Text vectorization (TF-IDF or word embeddings)
- **Hidden Layer**: Single fully connected layer with ReLU activation
- **Output Layer**: Binary classification (positive/negative sentiment)

## Quick Start

1. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Generate training data**:
   ```python
   python data/simulated_data.py
   ```

3. **Train the model**:
   ```python
   python src/train.py
   ```

4. **Evaluate performance**:
   ```python
   python src/evaluate.py
   ```

## Usage Example

```python
from src.model import SentimentClassifier
from src.data_loader import preprocess_text

# Load trained model
model = SentimentClassifier.load('model.pth')

# Analyze sentiment
text = "This movie was absolutely fantastic!"
processed_text = preprocess_text(text)
sentiment = model.predict(processed_text)
print(f"Sentiment: {'Positive' if sentiment > 0.5 else 'Negative'}")
```

## Training Data

The repository includes simulated training examples covering:
- **Positive samples**: Reviews, comments, and feedback with positive sentiment
- **Negative samples**: Critical reviews and negative feedback
- **Balanced dataset**: Equal distribution of positive and negative examples
- **Preprocessing**: Tokenization, lowercasing, and stop word removal

## Performance

Expected performance on simulated data:
- **Accuracy**: ~85-90%
- **Training time**: < 5 minutes on CPU
- **Model size**: < 1MB

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

MIT License - see LICENSE file for details.

## Acknowledgments

- PyTorch team for the excellent deep learning framework
- Scikit-learn for preprocessing utilities