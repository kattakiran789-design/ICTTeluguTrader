import os
import base64
import streamlit as st
import yfinance as yf
from openai import OpenAI
from PIL import Image

# 1. Page Configuration
st.set_page_config(
    page_title="తెలుగు స్మార్ట్ మనీ AI (Grok)",
    page_icon="📈",
    layout="wide",
    initial_sidebar_state="expanded"
)

# 2. System Prompt Loader
def load_system_prompt():
    file_path = "system_prompt.txt"
    if os.path.exists(file_path):
        with open(file_path, "r", encoding="utf-8") as f:
            return f.read()
    else:
        return (
            "You are 'Telugu Smart Money AI', an expert institutional trader specializing in Indian Stock Markets. "
            "Always reply in professional natural Telugu script. Provide entries, stop losses, and targets using "
            "Smart Money Concepts (SMC), ICT, and Supply/Demand analysis."
        )

SYSTEM_PROMPT = load_system_prompt()

# 3. Indian Market Ticker Mapping
TICKER_MAP = {
    "NIFTY 50": "^NSEI",
    "NIFTY": "^NSEI",
    "BANK NIFTY": "^NSEBANK",
    "BANKNIFTY": "^NSEBANK",
    "FIN NIFTY": "NIFTY_FIN_SERVICE.NS",
    "FINNIFTY": "NIFTY_FIN_SERVICE.NS",
    "SENSEX": "^BSESN"
}

# 4. Real-Time Data Fetcher
def get_realtime_market_data(symbol_or_name):
    try:
        clean_input = symbol_or_name.strip().upper()
        if clean_input in TICKER_MAP:
            ticker_symbol = TICKER_MAP[clean_input]
        elif not (clean_input.startswith("^") or clean_input.endswith(".NS") or clean_input.endswith(".BO")):
            ticker_symbol = f"{clean_input}.NS"
        else:
            ticker_symbol = clean_input

        stock = yf.Ticker(ticker_symbol)
        df = stock.history(period="5d", interval="1m")
        if df.empty:
            df = stock.history(period="5d")

        if not df.empty:
            ltp = round(df['Close'].iloc[-1], 2)
            prev_close = df['Close'].iloc[-2] if len(df) > 1 else ltp
            change = round(ltp - prev_close, 2)
            p_change = round((change / prev_close) * 100, 2)
            high = round(df['High'].max(), 2)
            low = round(df['Low'].min(), 2)
            
            return {
                "success": True,
                "symbol": ticker_symbol,
                "ltp": ltp,
                "change": change,
                "p_change": p_change,
                "high": high,
                "low": low
            }
        else:
            return {"success": False, "error": "డేటా దొరకలేదు."}
    except Exception as e:
        return {"success": False, "error": str(e)}

# Helper to encode uploaded images for Grok Vision
def encode_image(uploaded_file):
    return base64.b64encode(uploaded_file.getvalue()).decode('utf-8')

# 5. UI Styling
st.markdown("""
<style>
    .main-header { font-size: 2.2rem; color: #00E676; text-align: center; font-weight: 700; }
    .sub-header { font-size: 1rem; color: #AAA; text-align: center; margin-bottom: 20px; }
    .disclaimer-box {
        background-color: #1E232A; border-left: 4px solid #FFAB00;
        padding: 12px; border-radius: 4px; font-size: 0.85rem; color: #E0E0E0; margin-bottom: 20px;
    }
</style>
""", unsafe_allow_html=True)

# 6. Sidebar Configuration
with st.sidebar:
    st.title("⚡ Grok API సెట్టింగ్స్")
    
    grok_api_key = st.text_input("Grok API Key నమోదు చేయండి:", type="password")
    
    market_focus = st.selectbox(
        "మార్కెట్ ఎంచుకోండి:",
        ["Nifty 50", "Bank Nifty", "Fin Nifty", "Sensex", "Individual Stocks (NSE)"]
    )
    
    timeframe = st.selectbox(
        "టైమ్‌ఫ్రేమ్:",
        ["1 Min / 5 Min (Scalping)", "15 Min / 1 Hour (Intraday)", "4 Hour / Daily (Swing)"]
    )

    st.markdown("---")
    if st.button("💬 Chat క్లియర్ చేయి", use_container_width=True):
        st.session_state.messages = []
        st.rerun()

# 7. Header Section
st.markdown("<h1 class='main-header'>తెలుగు స్మార్ట్ మనీ AI 📊 (Powered by Grok)</h1>", unsafe_allow_html=True)
st.markdown("<p class='sub-header'>ICT, Supply & Demand మరియు Live Market Data తో విశ్లేషణ</p>", unsafe_allow_html=True)

st.markdown("""
<div class='disclaimer-box'>
<b>⚠️ నిరాకరణ (Disclaimer):</b> ఇది కేవలం ఎడ్యుకేషనల్ AI టూల్. ఇది ఫైనాన్షియల్ అడ్వైస్ కాదు. మార్కెట్లలో రిస్క్ ఉంటుంది, మీ స్వంత విశ్లేషణ పాటించండి.
</div>
""", unsafe_allow_html=True)

# Session State
if "messages" not in st.session_state:
    st.session_state.messages = []

# Display Chat History
for msg in st.session_state.messages:
    with st.chat_message(msg["role"]):
        st.markdown(msg["content"])
        if "image" in msg and msg["image"] is not None:
            st.image(msg["image"], caption="అప్‌లోడ్ చేసిన చార్ట్", use_container_width=True)

# File Uploader for Charts
uploaded_file = st.file_uploader("📸 చార్ట్ స్క్రీన్‌షాట్ అప్‌లోడ్ చేయండి (Optional):", type=["jpg", "jpeg", "png"])
uploaded_image = Image.open(uploaded_file) if uploaded_file else None

if uploaded_image:
    st.image(uploaded_image, caption="విశ్లేషణ కోసం సిద్ధంగా ఉన్న చార్ట్", use_container_width=True)

# Chat Input
user_prompt = st.chat_input("మార్కెట్ లేదా స్టాక్ పేరు టైప్ చేయండి (ఉదా: Nifty, Reliance, Sensex)...")

if user_prompt:
    if not grok_api_key:
        st.error("⚠️ దయచేసి ఎడమవైపు సైడ్‌బార్‌లో మీ Grok API Key ని నమోదు చేయండి.")
    else:
        # Initialize xAI (Grok) Client
        client = OpenAI(
            api_key=grok_api_key,
            base_url="https://api.x.ai/v1"
        )

        # Realtime market context
        market_data = get_realtime_market_data(user_prompt)
        realtime_context = ""
        if market_data["success"]:
            realtime_context = f"""
[REAL-TIME MARKET DATA FETCHED]:
- Ticker: {market_data['symbol']}
- Current Live Price (LTP): ₹{market_data['ltp']}
- Today Change: {market_data['change']} ({market_data['p_change']}%)
- High: ₹{market_data['high']} | Low: ₹{market_data['low']}
"""
        else:
            realtime_context = f"[REAL-TIME DATA NOTE]: లైవ్ డేటా ఫెచ్ కాలేదు. ({market_data['error']})\n"

        # User Message Assembly
        user_message = {"role": "user", "content": user_prompt}
        if uploaded_image:
            user_message["image"] = uploaded_image

        st.session_state.messages.append(user_message)
        with st.chat_message("user"):
            st.markdown(user_prompt)
            if uploaded_image:
                st.image(uploaded_image, use_container_width=True)

        # Grok AI Response Generation
        with st.chat_message("assistant"):
            with st.spinner("Grok AI స్మార్ట్ మనీ కాన్సెప్ట్స్ మరియు లైవ్ డేటాతో విశ్లేషిస్తోంది..."):
                try:
                    prompt_text = f"{realtime_context}\n[User Context: Market={market_focus}, Timeframe={timeframe}]\nయూజర్ ప్రశ్న: {user_prompt}"

                    # Image upload ఉంటే grok-2-vision-1212, లేదంటే grok-2-latest వాడటం
                    if uploaded_file:
                        base64_img = encode_image(uploaded_file)
                        model_name = "grok-2-vision-1212"
                        messages_payload = [
                            {"role": "system", "content": SYSTEM_PROMPT},
                            {
                                "role": "user",
                                "content": [
                                    {"type": "text", "text": prompt_text},
                                    {
                                        "type": "image_url",
                                        "image_url": {"url": f"data:image/jpeg;base64,{base64_img}"}
                                    }
                                ]
                            }
                        ]
                    else:
                        model_name = "grok-2-latest"
                        messages_payload = [
                            {"role": "system", "content": SYSTEM_PROMPT},
                            {"role": "user", "content": prompt_text}
                        ]

                    # Call Grok API
                    response = client.chat.completions.create(
                        model=model_name,
                        messages=messages_payload,
                        temperature=0.3
                    )

                    ai_response = response.choices[0].message.content
                    st.markdown(ai_response)
                    st.session_state.messages.append({"role": "assistant", "content": ai_response})

                except Exception as e:
                    st.error(f"Error: {str(e)}")
