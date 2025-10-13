from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI()

class MatchRequest(BaseModel):
    user_vector: list
    candidates: list

@app.get("/test")
def test():
    return {"message": "FastAPI is running!"}

@app.post("/recommend")
def recommend(data: MatchRequest):
    u = np.array(data.user_vector)
    C = np.array(data.candidates)

    sims = cosine_similarity([u], C)[0]
    ranks = sims.argsort()[::-1]
    return {"ranking": ranks.tolist(), "scores": sims.tolist()} 