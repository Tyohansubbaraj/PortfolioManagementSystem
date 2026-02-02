from fastapi import FastAPI
from pydantic import BaseModel
import yfinance as yf

app = FastAPI()

class SymbolsRequest(BaseModel):
    symbols: list[str]

@app.post("/prices")
def get_current_prices(req: SymbolsRequest):
    prices = {}

    for symbol in req.symbols:
        try:
            ticker = yf.Ticker(symbol)
            fi = ticker.fast_info
            prices[symbol] = float(fi["lastPrice"])
        except Exception:
            prices[symbol] = None

    return prices
