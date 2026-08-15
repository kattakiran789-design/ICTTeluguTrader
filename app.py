import os
import streamlit as st
from openai import OpenAI

# Streamlit Page Config
st.set_page_config(page_title="తెలుగు స్మార్ట్ మనీ AI (Grok)", page_icon="📈")

st.title("తెలుగు స్మార్ట్ మనీ AI 📊 (Grok API)")

# Sidebar లో Grok API Key సేకరణ
with st.sidebar:
    st.title("⚙️ సెట్టింగ్స్")
    grok_api_key = st.text_input("Grok API Key నమోదు చేయండి:", type="password")

if not grok_api_key:
    st.info("దయచేసి ఎడమవైపు సైడ్‌బార్‌లో మీ Grok API Key ని నమోదు చేయండి.")
else:
    # xAI (Grok) కోసం Client ని సెటప్ చేయడం
    client = OpenAI(
        api_key=grok_api_key,
        base_url="https://api.x.ai/v1", # xAI API Base URL
    )

    # Chat History నిర్వహణ
    if "messages" not in st.session_state:
        st.session_state.messages = []

    for msg in st.session_state.messages:
        with st.chat_message(msg["role"]):
            st.markdown(msg["content"])

    # User Input
    user_prompt = st.chat_input("మార్కెట్ లేదా స్టాక్ సంబంధిత ప్రశ్న అడగండి...")

    if user_prompt:
        st.session_state.messages.append({"role": "user", "content": user_prompt})
        with st.chat_message("user"):
            st.markdown(user_prompt)

        # Grok AI Response Generation
        with st.chat_message("assistant"):
            with st.spinner("Grok AI విశ్లేషిస్తోంది..."):
                try:
                    response = client.chat.completions.create(
                        model="grok-beta", # లేదా grok-2-vision-1212
                        messages=[
                            {"role": "system", "content": "You are 'Telugu Smart Money AI'. Reply always in natural professional Telugu script."},
                            {"role": "user", "content": user_prompt}
                        ]
                    )
                    ai_response = response.choices[0].message.content
                    st.markdown(ai_response)
                    st.session_state.messages.append({"role": "assistant", "content": ai_response})

                except Exception as e:
                    st.error(f"Error: {str(e)}")
                    
