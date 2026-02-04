from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import yfinance as yf
from datetime import datetime, timedelta

# ----------------------------------
# App initialization
# ----------------------------------
app = FastAPI()

# ----------------------------------
# CORS (FIXES OPTIONS 405 ERROR)
# ----------------------------------
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],          # allow all origins (DEV)
    allow_credentials=True,
    allow_methods=["*"],          # allows OPTIONS, POST, GET
    allow_headers=["*"],
)

# =====================================================
# 1) CURRENT PRICE FOR MULTIPLE SYMBOLS
# =====================================================
class SymbolsRequest(BaseModel):
    symbols: list[str]

@app.post("/prices")
def get_current_prices(req: SymbolsRequest):
    prices = {}

    for symbol in req.symbols:
        try:
            ticker = yf.Ticker(symbol)
            fi = ticker.fast_info
            prices[symbol.upper()] = float(fi["lastPrice"])
        except Exception:
            prices[symbol.upper()] = None

    return prices


# =====================================================
# 2) BUY PRICE (LTP OF BUY DATE)
# =====================================================
class BuyPriceRequest(BaseModel):
    symbol: str
    buy_date: str  # YYYY-MM-DD

@app.post("/buy-price")
def get_buy_price(req: BuyPriceRequest):
    symbol = req.symbol.upper()
    buy_date = datetime.strptime(req.buy_date, "%Y-%m-%d")

    # Yahoo requires end date as next day
    start = buy_date.strftime("%Y-%m-%d")
    end = (buy_date + timedelta(days=1)).strftime("%Y-%m-%d")

    try:
        ticker = yf.Ticker(symbol)
        data = ticker.history(
            start=start,
            end=end,
            interval="1d"
        )

        if data.empty:
            return {"price": None}

        # Last trading price of that day
        price = float(data["Close"].iloc[-1])
        return {"price": price}

    except Exception:
        return {"price": None}


# =====================================================
# 3) CHART DATA: BUY DATE -> TODAY (DAILY CLOSE)
# =====================================================
class ChartRequest(BaseModel):
    symbol: str
    start_date: str  # YYYY-MM-DD

@app.post("/chart-data")
def get_chart_data(req: ChartRequest):
    symbol = req.symbol.upper()
    start_date = req.start_date

    try:
        # Validate date format
        datetime.strptime(start_date, "%Y-%m-%d")

        ticker = yf.Ticker(symbol)
        data = ticker.history(
            start=start_date,
            interval="1d"
        )

        if data.empty:
            return []

        chart = []
        for date, row in data.iterrows():
            chart.append({
                "date": date.strftime("%Y-%m-%d"),
                "price": float(row["Close"])
            })

        return chart

    except Exception:
        return []
